package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.ConditionBlock
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.scheduler.BukkitTask
import java.util.Collections
import java.util.IdentityHashMap
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * The registry of [HolderState]s, and the flush that applies everything marked on them.
 *
 * States are only touched on the main thread; marks from other threads are queued. Everything
 * marked during a tick is applied at the start of the next one, after the event that caused it.
 */
internal object HolderStates {
    internal class Settings(
        val cooldown: Long,
        val inventoryClickTimeout: Long,
        val repairInterval: Int,
        val maxNewStatesPerTick: Int,
        val entitiesEnabled: Boolean,
        val skipAFKPlayers: Boolean
    )

    private const val PLAYER_BUCKETS = 20

    private const val NPC_SWEEP_INTERVAL = 200

    private const val MAX_ADMISSION_ATTEMPTS = 200

    // Classloaders of libreforge-based plugins disabled at runtime; their code must not run again.
    private val unloadedClassLoaders: MutableSet<ClassLoader> = Collections.newSetFromMap(IdentityHashMap())

    private val states = HashMap<UUID, HolderState>()

    private val dirty = LinkedHashSet<HolderState>()

    private val offThreadMarks = ConcurrentLinkedQueue<() -> Unit>()

    private var nextFlushMarks = ArrayList<() -> Unit>()

    private val newStates = ArrayDeque<HolderState>()

    private val retrack = LinkedHashSet<UUID>()

    private var playerBuckets = Array(PLAYER_BUCKETS) { LinkedHashSet<HolderState>() }

    private var entityBuckets = Array(1) { LinkedHashSet<HolderState>() }

    private var flushing = false

    private var resetRequested = false

    private var creationIndex = 0L

    private var task: BukkitTask? = null

    private var shutdownSweepDone = false

    /**
     * The flush counter, used as the tick clock for ages.
     */
    var tick = 0
        private set

    var settings = Settings(0, 500, 600, 50, entitiesEnabled = true, skipAFKPlayers = true)
        private set

    // Published for reads from other threads.
    private class PublishedHolders(
        val all: List<ProvidedHolder>,
        val byProvider: Map<HolderProvider, List<ProvidedHolder>>
    )

    private val publishedHolders = ConcurrentHashMap<UUID, PublishedHolders>()

    private val publishedActiveEffects = ConcurrentHashMap<UUID, List<ProvidedEffectBlock>>()

    private val conditionResults = ConcurrentHashMap<UUID, Map<ConditionBlock<*>, Boolean>>()

    /**
     * Read settings from config. Called on every reload.
     */
    fun reloadSettings() {
        val config = plugin.configYml
        val interval = config.getInt("refresh.entities.interval").coerceAtLeast(1)

        settings = Settings(
            config.getInt("refresh.cooldown").toLong().coerceAtLeast(0),
            config.getInt("refresh.inventory-click.timeout").toLong().coerceAtLeast(0),
            config.getInt("refresh.repair-interval").takeIf { it > 0 } ?: 600,
            config.getInt("refresh.max-new-states-per-tick").takeIf { it > 0 } ?: 50,
            config.getBool("refresh.entities.enabled"),
            config.getBool("refresh.players.skip-afk-players")
        )

        if (interval != HolderPolling.entityInterval || entityBuckets.size != interval) {
            HolderPolling.entityInterval = interval
            entityBuckets = Array(interval) { LinkedHashSet() }
            for (state in states.values) {
                if (state.kind == StateKind.ENTITY) {
                    entityBuckets[bucketOf(state.uuid, interval)] += state
                }
            }
        }
    }

    /**
     * Start (or restart, after a reload cancelled it) the flush task, and track what exists.
     */
    fun start() {
        if (shutdownSweepDone) {
            return
        }

        task?.cancel()
        task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable { flush() }, 1, 1)

        if (!states.containsKey(GlobalDispatcher.uuid)) {
            create(GlobalDispatcher, StateKind.GLOBAL)
        }

        for (player in Bukkit.getOnlinePlayers()) {
            trackPlayer(player)
        }

        for (world in Bukkit.getWorlds()) {
            for (entity in world.livingEntities) {
                trackEntity(entity)
            }
        }
    }

    /*
    Marks. Callable from any thread; applied to states on the main thread.
     */

    private fun mark(action: () -> Unit) {
        when {
            !Bukkit.isPrimaryThread() -> offThreadMarks.add(action)
            flushing -> nextFlushMarks.add(action)
            else -> action()
        }
    }

    private inline fun withState(dispatcher: Dispatcher<*>, crossinline action: (HolderState) -> Unit) {
        val uuid = dispatcher.uuid
        mark {
            val state = states[uuid]
            if (state != null) {
                action(state)
                markDirty(state)
            }
        }
    }

    fun markDirty(state: HolderState) {
        if (state.admitted && states[state.uuid] === state) {
            dirty += state
        }
    }

    fun markProvider(dispatcher: Dispatcher<*>, provider: HolderProvider) =
        withState(dispatcher) { state ->
            if (provider in registeredHolderProviders) {
                state.dirtyProviders += provider
            }
        }

    fun markProviderEverywhere(provider: HolderProvider) = mark {
        if (provider in registeredHolderProviders) {
            for (state in states.values) {
                state.dirtyProviders += provider
                markDirty(state)
            }
        }
    }

    fun markAllProviders(dispatcher: Dispatcher<*>) =
        withState(dispatcher) { it.dirtyProviders.addAll(registeredHolderProviders) }

    /**
     * As [markAllProviders], but not delayed by `refresh.cooldown`.
     */
    fun forceMarkAllProviders(dispatcher: Dispatcher<*>) =
        withState(dispatcher) {
            it.dirtyProviders.addAll(registeredHolderProviders)
            it.bypassCooldown = true
        }

    /**
     * If [code] belongs to a libreforge-based plugin that was disabled at runtime.
     */
    fun isUnloaded(code: Any): Boolean =
        unloadedClassLoaders.isNotEmpty() && code.javaClass.classLoader in unloadedClassLoaders

    fun markConditionsAll(dispatcher: Dispatcher<*>) =
        withState(dispatcher) { it.conditionDirtyAll = true }

    fun markCondition(dispatcher: Dispatcher<*>, condition: Condition<*>) =
        withState(dispatcher) { it.markConditionHolders(setOf(condition)) }

    /**
     * Signal a built-in [change] on a [dispatcher].
     */
    fun signal(dispatcher: Dispatcher<*>, change: HolderChange) =
        withState(dispatcher) {
            it.applySignal(change)
            if (change == HolderChange.Respawn || change == HolderChange.WorldChange) {
                it.reloadAll = true
            }
        }

    /**
     * Signal an inventory click, rate limited by `refresh.inventory-click.timeout`.
     */
    fun signalInventoryClick(dispatcher: Dispatcher<*>) =
        withState(dispatcher) { it.clickPending = true }

    fun onProviderRegistered(provider: HolderProvider) = markProviderEverywhere(provider)

    /**
     * Disable and re-enable every active effect from the current configuration, once, in the next
     * flush however many times it is requested.
     */
    fun resetAllStates() = mark {
        resetRequested = true
    }

    /*
    Tracking. Main thread only.
     */

    fun isTracked(uuid: UUID): Boolean = states.containsKey(uuid)

    fun trackPlayer(player: Player) {
        if (shutdownSweepDone || !player.isRealPlayer || states.containsKey(player.uniqueId)) {
            return
        }

        create(player.toDispatcher(), StateKind.PLAYER)
    }

    /**
     * Track a mob or NPC player, stripping stale attribute modifiers before its effects are enabled.
     */
    fun trackEntity(entity: LivingEntity, isNPC: Boolean = false) {
        if (shutdownSweepDone || !settings.entitiesEnabled) {
            return
        }

        if (entity is Player && !isNPC && entity.isRealPlayer) {
            return
        }

        // Spawn events fire before the entity is in the world, so validity is checked on admission.
        if (entity.isDead) {
            return
        }

        val existing = states[entity.uniqueId]
        if (existing != null) {
            if (existing.dispatcher.dispatcher === entity) {
                return
            }

            // A new entity object with the same UUID, e.g. after a dimension change.
            remove(existing)
        }

        create(entity.toDispatcher(), StateKind.ENTITY).needsCleanup = true
    }

    /**
     * Stop tracking [entity], disabling its active effects.
     */
    fun untrackEntity(entity: LivingEntity, isNPC: Boolean = false) {
        if (entity is Player && !isNPC && entity.isRealPlayer) {
            return
        }

        val state = states[entity.uniqueId] ?: return
        if (state.dispatcher.dispatcher === entity) {
            remove(state)
        }
    }

    /**
     * Stop tracking a player, disabling its active effects.
     */
    fun untrackPlayer(player: Player) {
        val state = states[player.uniqueId] ?: return
        remove(state)
    }

    /**
     * Re-track an entity by UUID in the next flush.
     */
    fun retrack(uuid: UUID) {
        retrack += uuid
    }

    private fun create(dispatcher: Dispatcher<*>, kind: StateKind): HolderState {
        val state = HolderState(dispatcher, kind, creationIndex++)
        states[state.uuid] = state

        // Holders are evaluated when first provided, and effects were just enabled.
        state.conditionsCheckedAt = tick
        state.repairedAt = tick

        state.dirtyProviders.addAll(registeredHolderProviders)

        if (kind == StateKind.ENTITY) {
            entityBuckets[bucketOf(state.uuid, entityBuckets.size)] += state
            newStates.addLast(state)
        } else {
            playerBuckets[bucketOf(state.uuid, PLAYER_BUCKETS)] += state
            state.admitted = true
            markDirty(state)
        }

        return state
    }

    private fun remove(state: HolderState) {
        state.isRemoved = true

        if (states[state.uuid] === state) {
            states.remove(state.uuid)
        }

        dirty.remove(state)
        playerBuckets[bucketOf(state.uuid, PLAYER_BUCKETS)].remove(state)
        entityBuckets[bucketOf(state.uuid, entityBuckets.size)].remove(state)

        state.disableAll()

        if (!states.containsKey(state.uuid)) {
            publishedHolders.remove(state.uuid)
            publishedActiveEffects.remove(state.uuid)
            conditionResults.remove(state.uuid)
        }
    }

    private fun bucketOf(uuid: UUID, buckets: Int): Int =
        (uuid.leastSignificantBits.toInt() and Int.MAX_VALUE) % buckets

    /*
    The flush.
     */

    private fun flush() {
        if (shutdownSweepDone) {
            return
        }

        tick++

        while (true) {
            val action = offThreadMarks.poll() ?: break
            guarded("apply a mark", action)
        }

        val marks = nextFlushMarks
        nextFlushMarks = ArrayList()
        marks.forEach { guarded("apply a mark", it) }

        flushing = true
        try {
            if (resetRequested) {
                resetRequested = false
                beginReset()
            }

            retrackEntities()

            if (tick % NPC_SWEEP_INTERVAL == 0 && !HolderLifecycle.hasEntityAddEvent) {
                sweepNPCs()
            }

            visitBuckets()
            admitNewStates()
            processDirty()
        } finally {
            flushing = false
        }
    }

    private inline fun guarded(what: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            plugin.logger.warning("Failed to $what")
            e.printStackTrace()
        } catch (e: LinkageError) {
            plugin.logger.warning("Failed to $what: ${e.message}")
        }
    }

    private fun beginReset() {
        if (!settings.entitiesEnabled) {
            for (state in states.values.filter { it.kind == StateKind.ENTITY }) {
                remove(state)
            }
        }

        for (state in states.values.sortedBy { it.creationIndex }) {
            state.resetPending = true

            if (state.kind == StateKind.ENTITY) {
                // Spread large mob counts over ticks, in the same budget as new states.
                if (state.admitted) {
                    state.admitted = false
                    dirty.remove(state)
                    newStates.addLast(state)
                }
            } else {
                markDirty(state)
            }
        }
    }

    private fun retrackEntities() {
        if (retrack.isEmpty()) {
            return
        }

        val uuids = retrack.toList()
        retrack.clear()

        for (uuid in uuids) {
            val entity = Bukkit.getEntity(uuid) as? LivingEntity ?: continue
            trackEntity(entity)
        }
    }

    private fun sweepNPCs() {
        if (!settings.entitiesEnabled) {
            return
        }

        for (world in Bukkit.getWorlds()) {
            for (entity in world.entities) {
                if (entity is Player && !entity.isRealPlayer && !states.containsKey(entity.uniqueId)) {
                    trackEntity(entity)
                }
            }
        }
    }

    private fun visitBuckets() {
        val repairInterval = settings.repairInterval

        for (state in playerBuckets[tick % PLAYER_BUCKETS].toList()) {
            if (state.kind == StateKind.PLAYER && settings.skipAFKPlayers) {
                val player = state.dispatcher.dispatcher as Player
                if (AFKManager.isAfk(player)) {
                    continue
                }
            }

            guarded("visit holders for ${state.uuid}") { state.visit(tick, repairInterval) }
            if (state.hasWork) {
                markDirty(state)
            }
        }

        for (state in entityBuckets[tick % entityBuckets.size].toList()) {
            if (!state.admitted) {
                continue
            }

            val entity = state.dispatcher.dispatcher as LivingEntity
            if (!entity.isValid) {
                remove(state)
                continue
            }

            guarded("visit holders for ${state.uuid}") { state.visit(tick, repairInterval) }
            if (state.hasWork) {
                markDirty(state)
            }
        }
    }

    private fun admitNewStates() {
        var admitted = 0
        var remaining = newStates.size

        while (admitted < settings.maxNewStatesPerTick && remaining-- > 0) {
            val state = newStates.removeFirstOrNull() ?: break

            if (states[state.uuid] !== state || state.admitted) {
                continue
            }

            val entity = state.dispatcher.dispatcher as LivingEntity
            if (entity.isDead) {
                remove(state)
                continue
            }

            // Not in the world yet; removals are caught by events. Given up on if it never arrives.
            if (!entity.isValid) {
                if (++state.admissionAttempts < MAX_ADMISSION_ATTEMPTS) {
                    newStates.addLast(state)
                } else {
                    remove(state)
                }
                continue
            }

            if (state.needsCleanup) {
                state.needsCleanup = false
                entity.removeEcoAttributeModifiers()
            }

            state.admitted = true
            markDirty(state)
            admitted++
        }
    }

    private fun processDirty() {
        if (dirty.isEmpty()) {
            return
        }

        val batch = dirty.toList()
        dirty.clear()

        val now = System.currentTimeMillis()
        val settings = settings

        for (state in batch) {
            if (states[state.uuid] !== state || !state.admitted) {
                continue
            }

            var heldBack = false
            guarded("update holders for ${state.uuid}") {
                heldBack = state.update(tick, now, settings)
            }

            if (heldBack) {
                dirty += state
            }
        }
    }

    /*
    Reads.
     */

    /**
     * The holders of a [dispatcher]: the state's immutable snapshot, off-thread the published one,
     * and asked on demand, storing nothing, for untracked dispatchers.
     */
    fun holders(dispatcher: Dispatcher<*>): List<ProvidedHolder> {
        if (!Bukkit.isPrimaryThread()) {
            return publishedHolders[dispatcher.uuid]?.all ?: askOnDemand(dispatcher)
        }

        val state = states[dispatcher.uuid] ?: return askOnDemand(dispatcher)
        state.ensureAsked(tick)
        return state.holderSnapshot
    }

    private fun askOnDemand(dispatcher: Dispatcher<*>): List<ProvidedHolder> {
        val entity = dispatcher.dispatcher
        // As before: only non-player entities are empty with entity refresh disabled.
        if (entity is LivingEntity && entity !is Player && !settings.entitiesEnabled) {
            return emptyList()
        }

        return registeredHolderProviders.toList().flatMap { it.ask(dispatcher) }
    }

    /**
     * The stored answer of [provider] for a tracked [dispatcher], or null if it must be scanned.
     */
    fun storedAnswer(dispatcher: Dispatcher<*>, provider: HolderProvider): Collection<ProvidedHolder>? {
        if (!Bukkit.isPrimaryThread()) {
            return publishedHolders[dispatcher.uuid]?.byProvider?.get(provider)
        }

        if (provider !in registeredHolderProviders) {
            return null
        }

        val state = states[dispatcher.uuid] ?: return null
        return state.answerOf(provider, tick)
    }

    fun providedActiveEffects(dispatcher: Dispatcher<*>): List<ProvidedEffectBlock> {
        if (!Bukkit.isPrimaryThread()) {
            return publishedActiveEffects[dispatcher.uuid] ?: emptyList()
        }

        return states[dispatcher.uuid]?.providedActiveEffects ?: emptyList()
    }

    /**
     * The cached placeholders of [holder] on a tracked [dispatcher], or null to generate them.
     */
    fun cachedPlaceholders(dispatcher: Dispatcher<*>, holder: Holder): List<InjectablePlaceholder>? {
        if (!Bukkit.isPrimaryThread()) {
            return null
        }

        return states[dispatcher.uuid]?.placeholdersFor(holder)
    }

    fun publishHolders(state: HolderState) {
        if (states[state.uuid] === state) {
            publishedHolders[state.uuid] = PublishedHolders(state.holderSnapshot, state.answersByProvider())
        }
    }

    fun publishActiveEffects(state: HolderState) {
        if (states[state.uuid] === state) {
            publishedActiveEffects[state.uuid] = state.providedActiveEffects
        }
    }

    /*
    Condition results, for reads from other threads. Only tracked dispatchers are stored.
     */

    fun conditionResult(dispatcher: Dispatcher<*>, block: ConditionBlock<*>): Boolean? =
        conditionResults[dispatcher.uuid]?.get(block)

    fun recordConditionResult(dispatcher: Dispatcher<*>, block: ConditionBlock<*>, isMet: Boolean) {
        val uuid = dispatcher.uuid
        if (!states.containsKey(uuid)) {
            return
        }

        val current = conditionResults[uuid]
        if (current != null && current[block] == isMet) {
            return
        }

        conditionResults[uuid] = if (current == null) mapOf(block to isMet) else current + (block to isMet)
    }

    fun clearConditionResults(uuid: UUID) {
        conditionResults.remove(uuid)
    }

    /*
    Shutdown and plugin disable.
     */

    /**
     * Disable every active effect and drop every state. Idempotent.
     */
    fun shutdownSweep() {
        if (shutdownSweepDone) {
            return
        }

        shutdownSweepDone = true
        HolderLifecycle.isSweeping = true

        try {
            try {
                task?.cancel()
            } catch (_: Exception) {
                // The scheduler may already refuse.
            }
            task = null

            for (state in states.values.toList()) {
                try {
                    state.disableAll()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            states.clear()
            dirty.clear()
            newStates.clear()
            retrack.clear()
            offThreadMarks.clear()
            nextFlushMarks.clear()
            publishedHolders.clear()
            publishedActiveEffects.clear()
            conditionResults.clear()
        } finally {
            HolderLifecycle.isSweeping = false
        }
    }

    /**
     * Handle a libreforge-based [disabledPlugin] being disabled: the shutdown sweep while the server is
     * stopping, otherwise only that plugin's effects and providers are removed.
     */
    fun onPluginDisable(disabledPlugin: EcoPlugin) {
        if (shutdownSweepDone) {
            return
        }

        if (HolderLifecycle.isStopping()) {
            shutdownSweep()
            return
        }

        val classLoader = disabledPlugin.javaClass.classLoader
        unloadedClassLoaders += classLoader
        val removed = unregisterHolderProviders { it.ownerClass.classLoader === classLoader }
        HolderSignals.unregisterProviders(removed)
        unregisterHolderFunctions(classLoader)

        for (state in states.values.toList()) {
            state.disableOwnedBy(classLoader, removed)
        }

        plugin.logger.warning(
            "${disabledPlugin.name} was disabled while the server is running; its effects have been disabled. " +
                    "A restart is required to use it again."
        )
    }
}

/**
 * Routes change signals to the providers and conditions that declared them.
 */
internal object HolderSignals {
    private val providersBySignal = HashMap<HolderChange, MutableList<HolderProvider>>()

    private val conditionsBySignal = HashMap<HolderChange, MutableSet<Condition<*>>>()

    private class CustomHandler(
        val owner: Any,
        val action: (Event) -> Unit
    )

    private val customHandlers = HashMap<Class<out Event>, MutableList<CustomHandler>>()

    private val listening = mutableSetOf<Class<out Event>>()

    private val registeredConditions: MutableSet<Condition<*>> = Collections.newSetFromMap(IdentityHashMap())

    private object SignalListener : Listener

    fun providersFor(change: HolderChange): List<HolderProvider> =
        providersBySignal[change] ?: emptyList()

    fun conditionsFor(change: HolderChange): Set<Condition<*>> =
        conditionsBySignal[change] ?: emptySet()

    fun registerProvider(provider: HolderProvider) {
        for (change in provider.invalidatedBy) {
            if (change is HolderChange.Custom<*>) {
                addCustom(change, provider) { HolderStates.markProvider(it, provider) }
            } else {
                providersBySignal.getOrPut(change) { mutableListOf() } += provider
            }
        }
    }

    fun registerCondition(condition: Condition<*>) {
        // Registration re-runs on every reload.
        if (!registeredConditions.add(condition)) {
            return
        }

        val signals = condition.invalidatedBy ?: return

        for (change in signals) {
            if (change is HolderChange.Custom<*>) {
                addCustom(change, condition) { HolderStates.markCondition(it, condition) }
            } else {
                conditionsBySignal.getOrPut(change) { mutableSetOf() } += condition
            }
        }
    }

    fun unregisterProviders(providers: Collection<HolderProvider>) {
        val removed: MutableSet<Any> = Collections.newSetFromMap(IdentityHashMap())
        removed.addAll(providers)

        for (list in providersBySignal.values) {
            list.removeAll { it in removed }
        }

        for (handlers in customHandlers.values) {
            handlers.removeAll { it.owner in removed }
        }
    }

    private fun addCustom(change: HolderChange.Custom<*>, owner: Any, mark: (Dispatcher<*>) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val dispatcherOf = change.dispatcherOf as (Event) -> Dispatcher<*>?

        customHandlers.getOrPut(change.event) { mutableListOf() } += CustomHandler(owner) { event ->
            dispatcherOf(event)?.let(mark)
        }

        val eventClass = change.event
        plugin.runWhenEnabled {
            if (listening.add(eventClass)) {
                Bukkit.getPluginManager().registerEvent(
                    eventClass,
                    SignalListener,
                    EventPriority.MONITOR,
                    EventExecutor { _, event ->
                        if (eventClass.isInstance(event)) {
                            customHandlers[eventClass]?.toList()?.forEach { it.action(event) }
                        }
                    },
                    plugin,
                    true
                )
            }
        }
    }
}
