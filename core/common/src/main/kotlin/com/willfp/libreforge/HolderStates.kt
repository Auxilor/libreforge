package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.ConditionBlock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
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
        val reloadInterval: Int,
        val maxNewStatesPerTick: Int,
        val entitiesEnabled: Boolean,
        val skipAFKPlayers: Boolean
    )

    private const val PLAYER_BUCKETS = 20

    private const val NPC_SWEEP_INTERVAL = 200

    private const val MAX_ADMISSION_ATTEMPTS = 200

    // Classloaders of libreforge-based plugins disabled at runtime; their code must not run again.
    @Volatile
    private var unloadedClassLoaders: Set<ClassLoader> = emptySet()

    // In creation order, which a reset follows.
    private val states = LinkedHashMap<UUID, HolderState>()

    private val dirty = LinkedHashSet<HolderState>()

    private val offThreadMarks = ConcurrentLinkedQueue<() -> Unit>()

    private var nextFlushMarks = ArrayList<() -> Unit>()

    private val newStates = ArrayDeque<HolderState>()

    private val retrack = LinkedHashSet<UUID>()

    private var playerBuckets = Array(PLAYER_BUCKETS) { LinkedHashSet<HolderState>() }

    private var entityBuckets = Array(1) { LinkedHashSet<HolderState>() }

    private var flushing = false

    private var flushThread: Thread? = null

    private val isFlushThread: Boolean
        get() = flushThread?.let { it === Thread.currentThread() } ?: Bukkit.isPrimaryThread()

    private var resetRequested = false

    private var task: EcoTask? = null

    private var shutdownSweepDone = false

    /**
     * The flush counter, used as the tick clock for ages.
     */
    var tick = 0
        private set

    private val defaultSettings = Settings(0, 500, 20, 600, 50, entitiesEnabled = true, skipAFKPlayers = true)

    @Volatile
    var settings = defaultSettings
        private set

    private class PublishedHolders(
        val all: List<ProvidedHolder>,
        val byProvider: Map<HolderProvider, List<ProvidedHolder>>
    )

    private val publishedHolders = ConcurrentHashMap<UUID, PublishedHolders>()

    private val publishedActiveEffects = ConcurrentHashMap<UUID, List<ProvidedEffectBlock>>()

    private val conditionResults = ConcurrentHashMap<UUID, ConcurrentHashMap<ConditionBlock<*>, Boolean>>()

    /**
     * Read settings from config. Called on every reload.
     */
    fun reloadSettings() {
        val config = plugin.configYml
        val interval = config.getInt("refresh.entities.interval").coerceAtLeast(1)

        settings = Settings(
            config.getInt("refresh.cooldown").toLong().coerceAtLeast(0),
            config.getInt("refresh.inventory-click.timeout").toLong().coerceAtLeast(0),
            config.getInt("refresh.repair-interval").takeIf { it > 0 } ?: defaultSettings.repairInterval,
            config.getInt("refresh.reload-interval").takeIf { it > 0 } ?: defaultSettings.reloadInterval,
            config.getInt("refresh.max-new-states-per-tick").takeIf { it > 0 } ?: defaultSettings.maxNewStatesPerTick,
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
        task = plugin.scheduler.global().runTimer(1, 1) { flush() }

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

    private fun mark(action: () -> Unit) {
        when {
            !isFlushThread -> offThreadMarks.add(action)
            flushing -> nextFlushMarks.add(action)
            else -> action()
        }
    }

    /**
     * Run [action] now on the flush thread, or queue it for the next flush.
     */
    private fun onFlushThread(action: () -> Unit) {
        if (isFlushThread) {
            action()
        } else {
            offThreadMarks.add(action)
        }
    }

    private inline fun withState(dispatcher: Dispatcher<*>, crossinline action: (HolderState) -> Unit) {
        mark {
            val state = stateFor(dispatcher)
            if (state != null) {
                action(state)
                markDirty(state)
            }
        }
    }

    /**
     * The state of [dispatcher], created on demand for a custom dispatcher that is not an entity.
     */
    private fun stateFor(dispatcher: Dispatcher<*>): HolderState? {
        val existing = states[dispatcher.uuid]

        if (existing != null && (existing.kind != StateKind.CUSTOM || existing.dispatcher.dispatcher === dispatcher.dispatcher)) {
            return existing
        }

        if (shutdownSweepDone || dispatcher.dispatcher is Entity || dispatcher === GlobalDispatcher) {
            return existing
        }

        // A new object behind the same custom dispatcher, e.g. a re-placed minion.
        if (existing != null) {
            remove(existing)
        }

        return create(dispatcher, StateKind.CUSTOM)
    }

    fun markDirty(state: HolderState) {
        if (state.admitted && states[state.uuid] === state) {
            dirty += state
        }
    }

    fun markProvider(dispatcher: Dispatcher<*>, provider: HolderProvider, scope: SignalScope? = null) =
        withState(dispatcher) { state ->
            if (provider in registeredHolderProviders) {
                state.markProvider(provider, scope)
            }
        }

    fun markProviderEverywhere(provider: HolderProvider) = mark {
        if (provider in registeredHolderProviders) {
            for (state in states.values) {
                state.markProvider(provider)
                markDirty(state)
            }
        }
    }

    /**
     * Mark [provider] on the dispatchers within [radius] of [location], on [owner], and on every
     * dispatcher it currently provides [holder] to.
     */
    fun markProviderNear(provider: HolderProvider, holder: Holder, owner: UUID, location: Location?, radius: Double) =
        mark {
            if (provider !in registeredHolderProviders) {
                return@mark
            }

            val nearby = hashSetOf(owner)
            val world = location?.world
            if (world != null) {
                world.getNearbyEntities(location, radius, radius, radius).mapTo(nearby) { it.uniqueId }
            }

            for (state in states.values) {
                if (state.uuid in nearby || state.provides(provider, holder)) {
                    state.markProvider(provider)
                    markDirty(state)
                }
            }
        }

    fun markAllProviders(dispatcher: Dispatcher<*>) =
        withState(dispatcher) { it.markProviders(registeredHolderProviders) }

    /**
     * As [markAllProviders], but not delayed by `refresh.cooldown`.
     */
    fun forceMarkAllProviders(dispatcher: Dispatcher<*>) =
        withState(dispatcher) {
            it.markProviders(registeredHolderProviders)
            it.bypassCooldown = true
        }

    /**
     * If [code] belongs to a libreforge-based plugin that was disabled at runtime.
     */
    fun isUnloaded(code: Any): Boolean {
        val loaders = unloadedClassLoaders
        return loaders.isNotEmpty() && code.javaClass.classLoader in loaders
    }

    fun markConditionsAll(dispatcher: Dispatcher<*>) =
        withState(dispatcher) { it.conditionDirtyAll = true }

    fun markCondition(dispatcher: Dispatcher<*>, condition: Condition<*>, scope: SignalScope? = null) =
        withState(dispatcher) { it.markConditionHolders(setOf(condition), scope) }

    /**
     * Signal a built-in [change] on a [dispatcher], touching only [scope], or everything if null.
     */
    fun signal(dispatcher: Dispatcher<*>, change: HolderChange, scope: SignalScope? = null) =
        withState(dispatcher) {
            it.applySignal(change, scope)
            if (change == HolderChange.Respawn || change == HolderChange.WorldChange) {
                it.reloadAll = true
            }
        }

    /**
     * Signal an inventory click touching [scope], or everything if null, rate limited by
     * `refresh.inventory-click.timeout`.
     */
    fun signalInventoryClick(dispatcher: Dispatcher<*>, scope: SignalScope? = null) =
        withState(dispatcher) { it.addPendingClick(scope) }

    /**
     * Disable and re-enable every active effect from the current configuration, once, in the next
     * flush however many times it is requested.
     */
    fun resetAllStates() = mark {
        resetRequested = true
    }

    fun trackPlayer(player: Player): Unit = onFlushThread {
        if (shutdownSweepDone || !player.isRealPlayer || states.containsKey(player.uniqueId)) {
            return@onFlushThread
        }

        create(player.toDispatcher(), StateKind.PLAYER)
    }

    /**
     * Track a mob or NPC player, stripping stale attribute modifiers before its effects are enabled.
     */
    fun trackEntity(entity: LivingEntity, isNPC: Boolean = false): Unit = onFlushThread {
        if (shutdownSweepDone || !settings.entitiesEnabled) {
            return@onFlushThread
        }

        if (!isNPC && entity.isRealPlayer) {
            return@onFlushThread
        }

        // Spawn events fire before the entity is in the world, so validity is checked on admission.
        if (entity.isDead) {
            return@onFlushThread
        }

        val existing = states[entity.uniqueId]
        if (existing != null) {
            if (existing.dispatcher.dispatcher === entity) {
                return@onFlushThread
            }

            // A new entity object with the same UUID, e.g. after a dimension change.
            remove(existing)
        }

        create(entity.toDispatcher(), StateKind.ENTITY).needsCleanup = true
    }

    /**
     * Stop tracking [entity], disabling its active effects.
     */
    fun untrackEntity(entity: LivingEntity, isNPC: Boolean = false): Unit = onFlushThread {
        if (!isNPC && entity.isRealPlayer) {
            return@onFlushThread
        }

        val state = states[entity.uniqueId] ?: return@onFlushThread
        if (state.dispatcher.dispatcher === entity) {
            remove(state)
        }
    }

    /**
     * Stop tracking a player, disabling its active effects.
     */
    fun untrackPlayer(player: Player): Unit = onFlushThread {
        val state = states[player.uniqueId] ?: return@onFlushThread
        remove(state)
    }

    /**
     * Re-track an entity by UUID in the next flush.
     */
    fun retrack(uuid: UUID): Unit = onFlushThread {
        retrack += uuid
    }

    private fun create(dispatcher: Dispatcher<*>, kind: StateKind): HolderState {
        val state = HolderState(dispatcher, kind)
        states[state.uuid] = state

        // Holders are evaluated when first provided, and effects were just enabled.
        state.conditionsCheckedAt = tick
        state.repairedAt = tick
        state.reloadedAt = tick

        state.markProviders(registeredHolderProviders)

        when (kind) {
            StateKind.ENTITY -> {
                entityBuckets[bucketOf(state.uuid, entityBuckets.size)] += state
                newStates.addLast(state)
            }

            // Custom dispatchers are never polled, only updated when marked.
            StateKind.CUSTOM -> {
                state.admitted = true
                markDirty(state)
            }

            else -> {
                playerBuckets[bucketOf(state.uuid, PLAYER_BUCKETS)] += state
                state.admitted = true
                markDirty(state)
            }
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

    private fun flush() {
        if (shutdownSweepDone) {
            return
        }

        tick++
        flushThread = Thread.currentThread()

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

        for (state in states.values.toList()) {
            state.resetPending = true

            when (state.kind) {
                // Spread large mob counts over ticks, in the same budget as new states.
                StateKind.ENTITY -> if (state.admitted) {
                    state.admitted = false
                    dirty.remove(state)
                    newStates.addLast(state)
                }

                StateKind.CUSTOM -> markDirty(state)

                // Players and the global dispatcher are reset when their bucket is next visited.
                else -> Unit
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

    // Reused each flush, as a visit may change its bucket.
    private val visiting = ArrayList<HolderState>()

    private fun visitBuckets() {
        for (state in snapshotOf(playerBuckets[tick % PLAYER_BUCKETS])) {
            if (state.kind == StateKind.PLAYER && settings.skipAFKPlayers && !state.resetPending) {
                val player = state.dispatcher.dispatcher as Player
                if (AFKManager.isAfk(player)) {
                    continue
                }
            }

            visit(state)
        }

        for (state in snapshotOf(entityBuckets[tick % entityBuckets.size])) {
            if (!state.admitted) {
                continue
            }

            val entity = state.dispatcher.dispatcher as LivingEntity
            if (!entity.isValid) {
                remove(state)
                continue
            }

            visit(state)
        }
    }

    private fun snapshotOf(bucket: Set<HolderState>): List<HolderState> {
        visiting.clear()
        bucket.forEach { visiting += it }
        return visiting
    }

    private fun visit(state: HolderState) {
        guarded("visit holders for ${state.uuid}") { state.visit(tick, settings) }
        if (state.hasWork) {
            markDirty(state)
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

        for (state in batch) {
            update(state, now)
        }
    }

    private fun update(state: HolderState, now: Long) {
        if (states[state.uuid] !== state || !state.admitted) {
            return
        }

        var heldBack = false
        guarded("update holders for ${state.uuid}") {
            heldBack = state.update(tick, now, settings)
        }

        if (heldBack) {
            dirty += state
        } else if (state.kind == StateKind.CUSTOM && state.isIdle && states[state.uuid] === state) {
            remove(state)
        }
    }

    /**
     * Apply what is marked on [dispatcher] now rather than in the next tick. Does nothing off the
     * main thread or during a holder update, where the changes are applied in the next tick.
     */
    fun flushNow(dispatcher: Dispatcher<*>) {
        if (!isFlushThread || flushing) {
            return
        }

        val state = states[dispatcher.uuid] ?: return
        if (!dirty.remove(state)) {
            return
        }

        flushing = true
        try {
            update(state, System.currentTimeMillis())
        } finally {
            flushing = false
        }
    }

    /**
     * The holders of a [dispatcher]: the state's immutable snapshot, off-thread the published one,
     * and asked on demand, storing nothing, for untracked dispatchers.
     */
    fun holders(dispatcher: Dispatcher<*>): List<ProvidedHolder> {
        if (!isFlushThread) {
            return publishedHolders[dispatcher.uuid]?.all ?: askOnDemand(dispatcher)
        }

        val state = states[dispatcher.uuid] ?: return askOnDemand(dispatcher)
        state.ensureAsked(tick)
        return state.holderSnapshot
    }

    private fun askOnDemand(dispatcher: Dispatcher<*>): List<ProvidedHolder> {
        val entity = dispatcher.dispatcher
        if (entity is LivingEntity && entity !is Player && !settings.entitiesEnabled) {
            return emptyList()
        }

        return registeredHolderProviders.toList().flatMap { it.ask(dispatcher) }
    }

    /**
     * The stored answer of [provider] for a tracked [dispatcher], or null if it must be scanned.
     */
    fun storedAnswer(dispatcher: Dispatcher<*>, provider: HolderProvider): Collection<ProvidedHolder>? {
        if (!isFlushThread) {
            return publishedHolders[dispatcher.uuid]?.byProvider?.get(provider)
        }

        if (provider !in registeredHolderProviders) {
            return null
        }

        val state = states[dispatcher.uuid] ?: return null
        return state.answerOf(provider, tick)
    }

    fun providedActiveEffects(dispatcher: Dispatcher<*>): List<ProvidedEffectBlock> {
        if (!isFlushThread) {
            return publishedActiveEffects[dispatcher.uuid] ?: emptyList()
        }

        return states[dispatcher.uuid]?.providedActiveEffects ?: emptyList()
    }

    /**
     * The cached placeholders of [holder] on a tracked [dispatcher], or null to generate them.
     */
    fun cachedPlaceholders(dispatcher: Dispatcher<*>, holder: Holder): List<InjectablePlaceholder>? {
        if (!isFlushThread) {
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

    fun conditionResult(dispatcher: Dispatcher<*>, block: ConditionBlock<*>): Boolean? =
        conditionResults[dispatcher.uuid]?.get(block)

    /**
     * Keep a condition result for reads from other threads. Only tracked dispatchers are stored.
     */
    fun recordConditionResult(dispatcher: Dispatcher<*>, block: ConditionBlock<*>, isMet: Boolean) {
        val uuid = dispatcher.uuid
        val results = conditionResults[uuid] ?: run {
            if (!states.containsKey(uuid)) {
                return
            }

            conditionResults.computeIfAbsent(uuid) { ConcurrentHashMap() }
        }

        // Reads take no lock; a write only when the result changed.
        val current = results[block]
        if (current == null || current != isMet) {
            results[block] = isMet
        }
    }

    fun clearConditionResults(uuid: UUID) {
        conditionResults.remove(uuid)
    }

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
        unloadedClassLoaders = Collections.newSetFromMap(IdentityHashMap<ClassLoader, Boolean>()).apply {
            addAll(unloadedClassLoaders)
            add(classLoader)
        }
        val removed = unregisterHolderProviders { it.ownerClass.classLoader === classLoader }
        HolderSignals.unregisterOwnedBy(classLoader, removed)
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
