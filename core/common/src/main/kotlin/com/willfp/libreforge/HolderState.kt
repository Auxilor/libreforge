package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.ConditionBlock
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.ChainElement
import com.willfp.libreforge.effects.EffectBlock
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.Integrity
import com.willfp.libreforge.effects.ProviderBinding
import com.willfp.libreforge.slot.SlotItemProvidedHolder
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import java.util.Collections
import java.util.IdentityHashMap
import java.util.UUID

/**
 * Identity of a provided holder: the provider, the holder id, and the index among that provider's
 * holders with the same id, in [HolderProvider.provide] order. The slot is not part of the key, so
 * an item moved between slots keeps its key.
 */
internal data class HolderKey(
    val providerId: String,
    val holderId: NamespacedKey,
    val occurrence: Int
)

/**
 * Identity of one enabled effect element.
 */
internal data class EffectKey(
    val holder: HolderKey,
    val blockIndex: Int,
    val elementIndex: Int
) {
    val discriminator: String
        get() = "${holder.providerId}|${holder.holderId}|$blockIndex|$elementIndex|${holder.occurrence}"
}

/**
 * An enabled effect element. Keeps the compiled element alive after its plugin reloads, and the
 * exact provided holder it was last applied with, which every disable uses.
 */
internal class ActiveEffect(
    val key: EffectKey,
    val block: EffectBlock,
    val element: ChainElement<*>,
    val identifiers: Identifiers,
    var enabledWith: ProvidedHolder
) {
    val isPermanent: Boolean
        get() = block.isPermanent
}

/**
 * A provider that is re-asked by scanning, with [HolderProvider.provide] served from state.
 */
internal interface ScanningHolderProvider {
    fun scan(dispatcher: Dispatcher<*>): Collection<ProvidedHolder>
}

internal fun HolderProvider.ask(dispatcher: Dispatcher<*>): Collection<ProvidedHolder> =
    (this as? ScanningHolderProvider)?.scan(dispatcher) ?: this.provide(dispatcher)

/**
 * Per-holder data for one dispatcher: the generated placeholders, and what is derived from them.
 */
internal class HolderData(
    providedHolder: ProvidedHolder,
    dispatcher: Dispatcher<*>
) {
    val placeholders: List<InjectablePlaceholder>

    private val placeholderNames: Set<String>

    init {
        val values = providedHolder.generatePlaceholders(dispatcher)
        placeholders = values.mapToPlaceholders()
        placeholderNames = values.flatMapTo(mutableSetOf()) { it.identifiers }
    }

    private val dynamicElements = IdentityHashMap<ChainElement<*>, Boolean>()

    private val polledConditions = IdentityHashMap<ConditionBlock<*>, Boolean>()

    private var conditionTypes: Set<Condition<*>>? = null

    private var isPolled: Boolean? = null

    /**
     * If an effect [element] must be reloaded on every condition pass for this holder.
     */
    fun isDynamic(element: ChainElement<*>): Boolean =
        dynamicElements.getOrPut(element) { DynamicConfigs.isDynamic(element.config, placeholderNames) }

    private fun isPolled(block: ConditionBlock<*>): Boolean =
        polledConditions.getOrPut(block) {
            block.condition.invalidatedBy == null || DynamicConfigs.isDynamic(block.config, placeholderNames)
        }

    /**
     * If [holder] has a polled condition or a dynamic effect, so must be re-evaluated periodically.
     */
    fun isPolled(holder: Holder): Boolean {
        isPolled?.let { return it }

        val hasPolledCondition = holder.conditions.any { isPolled(it) }
                || holder.effects.any { block -> block.conditions.any { isPolled(it) } }

        val hasDynamicEffect = holder.effects.any { block ->
            block.isPermanent && block.effects.any { it.effect.alwaysReload || isDynamic(it) }
        }

        return (hasPolledCondition || hasDynamicEffect).also { isPolled = it }
    }

    /**
     * If [holder] uses any of [conditions], at holder or effect level.
     */
    fun usesAny(holder: Holder, conditions: Set<Condition<*>>): Boolean {
        val types = conditionTypes ?: buildSet {
            holder.conditions.forEach { add(it.condition) }
            holder.effects.forEach { block -> block.conditions.forEach { add(it.condition) } }
        }.also { conditionTypes = it }

        return types.any { it in conditions }
    }
}

/**
 * Decides whether a config can change value while its holder stays the same.
 */
internal object DynamicConfigs {
    // Matched exactly as eco's findPlaceholders.
    private val PLACEHOLDER = Regex("%[^% ]+%")

    /**
     * If any string in [config] contains a placeholder other than [ownPlaceholders], or `rand`
     * outside placeholders (eco's only impure built-ins are `rand` and `random`).
     */
    fun isDynamic(config: Config, ownPlaceholders: Set<String>): Boolean =
        anyString(config) { isDynamic(it, ownPlaceholders) }

    /**
     * If [value] (a config, list, or string) is dynamic by the same rule, with no own placeholders.
     */
    fun isDynamicValue(value: Any?): Boolean =
        anyString(value) { isDynamic(it, emptySet()) }

    /**
     * If any string in [config] references a placeholder.
     */
    fun hasPlaceholder(config: Config): Boolean =
        anyString(config) { '%' in it && PLACEHOLDER.containsMatchIn(it) }

    private fun isDynamic(value: String, ownPlaceholders: Set<String>): Boolean {
        var stripped = value

        if ('%' in stripped) {
            for (name in ownPlaceholders) {
                stripped = stripped.replace("%$name%", "")
            }

            if (PLACEHOLDER.containsMatchIn(stripped)) {
                return true
            }
        }

        return "rand" in stripped
    }

    private fun anyString(value: Any?, predicate: (String) -> Boolean): Boolean = when (value) {
        is String -> predicate(value)
        is Config -> value.getKeys(false).any { anyString(value.get(it), predicate) }
        is Map<*, *> -> value.values.any { anyString(it, predicate) }
        is Iterable<*> -> value.any { anyString(it, predicate) }
        else -> false
    }
}

internal enum class StateKind {
    PLAYER,
    GLOBAL,
    ENTITY
}

/**
 * Everything libreforge tracks for one dispatcher. Accessed only on the main thread.
 */
internal class HolderState(
    val dispatcher: Dispatcher<*>,
    val kind: StateKind,
    val creationIndex: Long
) {
    val uuid: UUID = dispatcher.uuid

    // Each provider's last answer, keyed. A provider with no entry has never been asked.
    private val answers = IdentityHashMap<HolderProvider, LinkedHashMap<HolderKey, ProvidedHolder>>()

    // The answers effects are currently applied from.
    private val applied = IdentityHashMap<HolderProvider, LinkedHashMap<HolderKey, ProvidedHolder>>()

    private var appliedByKey = HashMap<HolderKey, ProvidedHolder>()

    private var appliedOrder = emptyList<Pair<HolderKey, ProvidedHolder>>()

    private var appliedSnapshot = emptyList<ProvidedHolder>()

    private val providerCheckedAt = IdentityHashMap<HolderProvider, Int>()

    var conditionsCheckedAt = 0
    var repairedAt = 0
    var lastHolderUpdateAt = 0L
    var lastClickRefreshAt = 0L

    val dirtyProviders: MutableSet<HolderProvider> = Collections.newSetFromMap(IdentityHashMap())

    // Answered but not yet applied, e.g. asked by a read before the first flush.
    private val pendingProviders: MutableSet<HolderProvider> = Collections.newSetFromMap(IdentityHashMap())

    val conditionDirtyHolders = HashSet<HolderKey>()
    var conditionDirtyAll = false
    var repairDue = false
    var reloadAll = false
    var resetPending = false
    var clickPending = false

    // Set when the state is dropped, possibly by a handler in the middle of its own update.
    var isRemoved = false

    var admissionAttempts = 0

    var bypassCooldown = false

    // Non-player states wait for the new-state budget before their first flush.
    var admitted = false
    var needsCleanup = false

    private val active = LinkedHashMap<EffectKey, ActiveEffect>()

    var providedActiveEffects: List<ProvidedEffectBlock> = emptyList()
        private set

    var holderSnapshot: List<ProvidedHolder> = emptyList()
        private set

    private val holderData = IdentityHashMap<Holder, HolderData>()

    private val presentHolders: MutableSet<Holder> = Collections.newSetFromMap(IdentityHashMap())

    val hasWork: Boolean
        get() = dirtyProviders.isNotEmpty() || pendingProviders.isNotEmpty() || conditionDirtyAll
                || conditionDirtyHolders.isNotEmpty() || repairDue || reloadAll || resetPending || clickPending

    /**
     * The cached placeholders for [holder], or null if no provided holder in this state has it.
     */
    fun placeholdersFor(holder: Holder): List<InjectablePlaceholder>? {
        if (holder !in presentHolders) {
            return null
        }

        val data = holderData[holder] ?: run {
            val provided = findProvided(holder) ?: return null
            HolderData(provided, dispatcher).also { holderData[holder] = it }
        }

        return data.placeholders
    }

    private fun findProvided(holder: Holder): ProvidedHolder? {
        for ((_, ph) in appliedOrder) {
            if (ph.holder === holder) {
                return ph
            }
        }

        return holderSnapshot.firstOrNull { it.holder === holder }
    }

    private fun dataFor(ph: ProvidedHolder): HolderData =
        holderData.getOrPut(ph.holder) { HolderData(ph, dispatcher) }

    /**
     * The stored answer of [provider], asking it now if it has never been asked.
     */
    fun answerOf(provider: HolderProvider, tick: Int): Collection<ProvidedHolder> {
        if (!answers.containsKey(provider)) {
            ask(provider, tick)
            dirtyProviders.remove(provider)
            rebuildSnapshot()
            HolderStates.markDirty(this)
        }

        return answers[provider]?.values?.let { Collections.unmodifiableCollection(it) } ?: emptyList()
    }

    /**
     * Ask every provider that has never been asked, as reads must see the right holders.
     */
    fun ensureAsked(tick: Int) {
        var asked = false

        for (provider in registeredHolderProviders) {
            if (!answers.containsKey(provider)) {
                ask(provider, tick)
                dirtyProviders.remove(provider)
                asked = true
            }
        }

        if (asked) {
            rebuildSnapshot()
            HolderStates.markDirty(this)
        }
    }

    fun answersByProvider(): Map<HolderProvider, List<ProvidedHolder>> {
        val map = IdentityHashMap<HolderProvider, List<ProvidedHolder>>()
        for ((provider, keyed) in answers) {
            map[provider] = keyed.values.toList()
        }
        return map
    }

    private fun ask(provider: HolderProvider, tick: Int) {
        val providerId = registeredProviderId(provider)
        val occurrences = HashMap<NamespacedKey, Int>()
        val keyed = LinkedHashMap<HolderKey, ProvidedHolder>()

        for (ph in provider.ask(dispatcher)) {
            val holderId = ph.holder.id
            val occurrence = occurrences.getOrDefault(holderId, 0)
            occurrences[holderId] = occurrence + 1
            keyed[HolderKey(providerId, holderId, occurrence)] = ph
        }

        answers[provider] = keyed
        providerCheckedAt[provider] = tick
        pendingProviders += provider
    }

    private fun rebuildSnapshot() {
        val list = ArrayList<ProvidedHolder>()

        for (provider in registeredHolderProviders) {
            answers[provider]?.let { list.addAll(it.values) }
        }

        holderSnapshot = Collections.unmodifiableList(list)
        rebuildPresentHolders()
        HolderStates.publishHolders(this)
    }

    private fun rebuildApplied() {
        val order = ArrayList<Pair<HolderKey, ProvidedHolder>>()
        val byKey = HashMap<HolderKey, ProvidedHolder>()

        for (provider in registeredHolderProviders) {
            val keyed = applied[provider] ?: continue
            for ((key, ph) in keyed) {
                order += key to ph
                byKey[key] = ph
            }
        }

        appliedOrder = order
        appliedByKey = byKey
        appliedSnapshot = Collections.unmodifiableList(order.map { it.second })
        rebuildPresentHolders()
    }

    private fun rebuildPresentHolders() {
        presentHolders.clear()
        holderSnapshot.forEach { presentHolders += it.holder }
        appliedOrder.forEach { presentHolders += it.second.holder }
        holderData.keys.retainAll(presentHolders)
    }

    /**
     * Visit this state from its bucket: mark what has aged.
     */
    fun visit(tick: Int, repairInterval: Int) {
        for (provider in registeredHolderProviders) {
            val checkedAt = providerCheckedAt[provider] ?: continue
            val maxAge = provider.maxAge(dispatcher) ?: continue
            if (tick - checkedAt >= maxAge) {
                dirtyProviders += provider
            }
        }

        if (tick - conditionsCheckedAt >= HolderPolling.conditionMaxAge(dispatcher)) {
            conditionsCheckedAt = tick
            for ((key, ph) in appliedOrder) {
                if (dataFor(ph).isPolled(ph.holder)) {
                    conditionDirtyHolders += key
                }
            }
        }

        if (tick - repairedAt >= repairInterval) {
            repairDue = true
        }
    }

    /**
     * Mark the providers and conditions that a built-in [change] invalidates.
     */
    fun applySignal(change: HolderChange) {
        dirtyProviders.addAll(HolderSignals.providersFor(change))
        markConditionHolders(HolderSignals.conditionsFor(change))
    }

    fun markConditionHolders(conditions: Set<Condition<*>>) {
        if (conditions.isEmpty()) {
            return
        }

        for ((key, ph) in appliedOrder) {
            if (dataFor(ph).usesAny(ph.holder, conditions)) {
                conditionDirtyHolders += key
            }
        }
    }

    /**
     * Apply everything marked. Returns true if some work was held back by rate limiting.
     */
    fun update(tick: Int, now: Long, settings: HolderStates.Settings): Boolean {
        if (resetPending) {
            reset()
        }

        var heldBack = false

        if (clickPending) {
            if (lastClickRefreshAt == 0L || now - lastClickRefreshAt >= settings.inventoryClickTimeout) {
                clickPending = false
                lastClickRefreshAt = now
                applySignal(HolderChange.Items)
            } else {
                heldBack = true
            }
        }

        // 1. Re-ask dirty providers only.
        if (dirtyProviders.isNotEmpty()) {
            if (bypassCooldown || lastHolderUpdateAt == 0L || now - lastHolderUpdateAt >= settings.cooldown) {
                bypassCooldown = false
                dispatcher.runRefreshFunctions()

                for (provider in registeredHolderProviders) {
                    if (provider in dirtyProviders) {
                        ask(provider, tick)
                    }
                }

                dirtyProviders.clear()
                lastHolderUpdateAt = now
                rebuildSnapshot()
            } else {
                heldBack = true
            }
        }

        // 2-3. Classify the keys of the re-asked providers.
        val before = appliedSnapshot
        val added = ArrayList<Pair<HolderKey, ProvidedHolder>>()
        val removed = ArrayList<Pair<HolderKey, ProvidedHolder>>()
        val moved = HashSet<HolderKey>()
        val holdersChanged = pendingProviders.isNotEmpty()

        if (holdersChanged) {
            for (provider in pendingProviders) {
                val old = applied[provider] ?: emptyMap()
                val new = answers[provider] ?: LinkedHashMap()

                for ((key, ph) in new) {
                    val previous = old[key]
                    when {
                        previous == null -> added += key to ph
                        previous.holder != ph.holder -> {
                            removed += key to previous
                            added += key to ph
                        }

                        previous.slotType != ph.slotType -> moved += key
                    }
                }

                for ((key, previous) in old) {
                    if (!new.containsKey(key)) {
                        removed += key to previous
                    }
                }

                if (new.isEmpty()) {
                    applied.remove(provider)
                } else {
                    applied[provider] = new
                }
            }

            pendingProviders.clear()
            rebuildApplied()
        }

        // 4. Holder events, per key.
        if (added.isNotEmpty() || removed.isNotEmpty()) {
            val after = appliedSnapshot

            for ((_, ph) in added) {
                Bukkit.getPluginManager().callEvent(HolderEnableEvent(dispatcher, ph, after))
            }

            for ((_, ph) in removed) {
                Bukkit.getPluginManager().callEvent(HolderDisableEvent(dispatcher, ph, before))
            }

            @Suppress("DEPRECATION")
            Bukkit.getPluginManager().callEvent(HolderProvideEvent(dispatcher, after))
        }

        // 5. Choose the holders to evaluate.
        val toEvaluate = LinkedHashMap<HolderKey, ProvidedHolder>()
        for ((key, ph) in added) {
            toEvaluate[key] = ph
        }
        for (key in moved) {
            appliedByKey[key]?.let { toEvaluate[key] = it }
        }

        // Only a full pass stamps; periodic marks are stamped when made (visit), so a frequent partial
        // signal never starves the periodic check.
        val fullConditionPass = conditionDirtyAll
        if (conditionDirtyAll) {
            for ((key, ph) in appliedOrder) {
                toEvaluate[key] = ph
            }
        } else {
            for (key in conditionDirtyHolders) {
                appliedByKey[key]?.let { toEvaluate[key] = it }
            }
        }
        conditionDirtyAll = false
        conditionDirtyHolders.clear()

        // A handler may have removed this state (e.g. kicked the player); its effects are already disabled.
        if (isRemoved) {
            return false
        }

        // 5 (cont). Evaluate everything before changing anything, so a throwing condition leaves the
        // holder as it was.
        val plans = ArrayList<Pair<Pair<HolderKey, ProvidedHolder>, BooleanArray>>(toEvaluate.size)
        for ((key, ph) in toEvaluate) {
            val met = try {
                evaluate(ph)
            } catch (e: Exception) {
                plugin.logger.warning("Failed to evaluate conditions of ${ph.holder.id} for $uuid")
                e.printStackTrace()
                continue
            }

            plans += (key to ph) to met
        }

        // 6. Work out the effect delta.
        val disables = ArrayList<ActiveEffect>()
        val enables = ArrayList<ActiveEffect>()
        val reloads = LinkedHashMap<EffectKey, Pair<ActiveEffect, ProvidedHolder>>()

        if (removed.isNotEmpty()) {
            val removedKeys = removed.mapTo(HashSet()) { it.first }
            val iterator = active.values.iterator()
            while (iterator.hasNext()) {
                val activeEffect = iterator.next()
                if (activeEffect.key.holder in removedKeys) {
                    disables += activeEffect
                    iterator.remove()
                }
            }
        }

        for ((holderEntry, met) in plans) {
            val (key, ph) = holderEntry
            val data = dataFor(ph)

            ph.holder.effects.forEachIndexed { blockIndex, block ->
                block.effects.forEachIndexed { elementIndex, element ->
                    val effectKey = EffectKey(key, blockIndex, elementIndex)
                    val current = active[effectKey]
                    val isMet = met[blockIndex] && !HolderStates.isUnloaded(element.effect)

                    if (isMet && current == null) {
                        // Added to active only once enabled, so a removal mid-update never disables it unenabled.
                        enables += ActiveEffect(
                            effectKey,
                            block,
                            element,
                            element.effect.makeIdentifiers(effectKey.discriminator),
                            ph
                        )
                    } else if (!isMet && current != null) {
                        active.remove(effectKey)
                        disables += current
                    } else if (current != null && block.isPermanent) {
                        val rebind = key in moved && element.effect.providerBinding != ProviderBinding.NONE
                        if (rebind || element.effect.alwaysReload || data.isDynamic(element)) {
                            reloads[effectKey] = current to ph
                        }
                    }
                }
            }
        }

        // Unchanged holders whose item changed, for effects acting on the item.
        if (holdersChanged) {
            for (activeEffect in active.values) {
                if (!activeEffect.isPermanent || activeEffect.element.effect.providerBinding != ProviderBinding.ITEM) {
                    continue
                }

                if (activeEffect.key in reloads) {
                    continue
                }

                val current = appliedByKey[activeEffect.key.holder] ?: continue
                if (current.getProvider<ItemStack>() != activeEffect.enabledWith.getProvider<ItemStack>()) {
                    reloads[activeEffect.key] = activeEffect to current
                }
            }
        }

        // Disables always run, even if the state is removed meanwhile: these are no longer in active.
        for (activeEffect in disables.sortedBy { it.block.weight }) {
            safely(activeEffect, "disable") {
                activeEffect.element.disableActive(dispatcher, activeEffect.enabledWith, activeEffect.identifiers)
            }
        }

        for (activeEffect in enables.sortedBy { it.block.weight }) {
            if (isRemoved) {
                return false
            }

            active[activeEffect.key] = activeEffect
            safely(activeEffect, "enable") {
                activeEffect.element.enableActive(dispatcher, activeEffect.enabledWith, activeEffect.identifiers)
            }
        }

        for ((activeEffect, current) in reloads.values.sortedBy { it.first.block.weight }) {
            if (isRemoved) {
                return false
            }

            reload(activeEffect, current)
        }

        if (isRemoved) {
            return false
        }

        // 7. Rebuild what triggers read.
        if (disables.isNotEmpty() || enables.isNotEmpty() || holdersChanged) {
            rebuildProvidedActiveEffects()
        }

        // 9. Slow passes.
        if (reloadAll) {
            reloadAll = false
            repairDue = false
            repairedAt = tick
            reloadAllPermanent()
        } else if (repairDue) {
            repairDue = false
            repairedAt = tick
            repair()
        }

        // 10.
        if (fullConditionPass) {
            conditionsCheckedAt = tick
        }

        return heldBack
    }

    /**
     * Which of the holder's effect blocks are met. Effect-level conditions are skipped when the
     * holder's own conditions fail.
     */
    private fun evaluate(ph: ProvidedHolder): BooleanArray {
        val holder = ph.holder
        val met = BooleanArray(holder.effects.size)

        if (!areMet(holder.conditions, ph)) {
            return met
        }

        holder.effects.forEachIndexed { blockIndex, block ->
            met[blockIndex] = areMet(block.conditions, ph)
        }

        return met
    }

    private fun areMet(conditions: ConditionList, ph: ProvidedHolder): Boolean {
        // Conditions from a plugin disabled at runtime can no longer run.
        if (conditions.any { HolderStates.isUnloaded(it.condition) }) {
            return false
        }

        return conditions.areMet(dispatcher, ph)
    }

    private fun reload(activeEffect: ActiveEffect, current: ProvidedHolder): Boolean {
        var reloaded = false

        safely(activeEffect, "reload") {
            reloaded = activeEffect.element.reloadActive(
                dispatcher,
                activeEffect.enabledWith,
                current,
                activeEffect.identifiers
            )
        }

        // A skipped reload leaves the effect applied with the old provided holder.
        if (reloaded) {
            activeEffect.enabledWith = current
        }

        return reloaded
    }

    private fun repair(activeEffect: ActiveEffect, current: ProvidedHolder) {
        safely(activeEffect, "repair") {
            activeEffect.element.repairActive(dispatcher, activeEffect.enabledWith, current, activeEffect.identifiers)
            activeEffect.enabledWith = current
        }
    }

    private fun checkIntegrity(activeEffect: ActiveEffect): Integrity {
        var integrity = Integrity.UNKNOWN

        safely(activeEffect, "integrity check") {
            integrity = activeEffect.element.effect.checkIntegrity(
                dispatcher,
                activeEffect.identifiers,
                activeEffect.enabledWith
            )
        }

        return integrity
    }

    /**
     * Reload static permanent effects, or repair them if they report their applied state is gone.
     */
    private fun repair() {
        for (activeEffect in permanentEffectsByWeight()) {
            val current = appliedByKey[activeEffect.key.holder] ?: continue

            if (activeEffect.element.effect.alwaysReload || dataFor(current).isDynamic(activeEffect.element)) {
                continue
            }

            when (checkIntegrity(activeEffect)) {
                Integrity.INTACT -> Unit
                Integrity.MISSING -> repair(activeEffect, current)
                Integrity.UNKNOWN -> reload(activeEffect, current)
            }
        }
    }

    /**
     * Reload every permanent effect, then repair those reporting their applied state is gone.
     */
    private fun reloadAllPermanent() {
        for (activeEffect in permanentEffectsByWeight()) {
            val current = appliedByKey[activeEffect.key.holder] ?: continue

            reload(activeEffect, current)

            if (checkIntegrity(activeEffect) == Integrity.MISSING) {
                repair(activeEffect, current)
            }
        }
    }

    private fun permanentEffectsByWeight(): List<ActiveEffect> =
        active.values.filter { it.isPermanent }.sortedBy { it.block.weight }

    private fun rebuildProvidedActiveEffects() {
        val blocksByHolder = HashMap<HolderKey, MutableMap<Int, EffectBlock>>()

        for (activeEffect in active.values) {
            blocksByHolder.getOrPut(activeEffect.key.holder) { sortedMapOf() }[activeEffect.key.blockIndex] =
                activeEffect.block
        }

        val blocks = ArrayList<ProvidedEffectBlock>()

        for ((key, ph) in appliedOrder) {
            val holderBlocks = blocksByHolder[key] ?: continue
            for (block in holderBlocks.values) {
                blocks += ProvidedEffectBlock(block, ph)
            }
        }

        providedActiveEffects = Collections.unmodifiableList(blocks.sorted())
        HolderStates.publishActiveEffects(this)
    }

    /**
     * Disable every active effect with its old compiled element, and forget every holder so the
     * next update re-asks every provider and enables what is met from scratch.
     */
    private fun reset() {
        resetPending = false
        disableAll()

        answers.clear()
        applied.clear()
        pendingProviders.clear()
        providerCheckedAt.clear()
        holderData.clear()
        rebuildApplied()
        rebuildSnapshot()

        conditionDirtyAll = false
        conditionDirtyHolders.clear()
        dirtyProviders.addAll(registeredHolderProviders)
        HolderStates.clearConditionResults(uuid)
    }

    /**
     * Disable every active effect with the provided holder it was enabled with.
     */
    fun disableAll() {
        val toDisable = active.values.sortedBy { it.block.weight }
        active.clear()

        for (activeEffect in toDisable) {
            safely(activeEffect, "disable") {
                activeEffect.element.disableActive(dispatcher, activeEffect.enabledWith, activeEffect.identifiers)
            }
        }

        rebuildProvidedActiveEffects()
    }

    /**
     * Disable every active effect whose effect class was loaded by [classLoader] or whose holder
     * came from one of [removedProviders], and forget those providers.
     */
    fun disableOwnedBy(classLoader: ClassLoader, removedProviders: Collection<HolderProvider>) {
        val removedKeys = HashSet<HolderKey>()
        for (provider in removedProviders) {
            applied[provider]?.keys?.let { removedKeys.addAll(it) }
        }

        val toDisable = active.values.filter {
            it.element.effect.javaClass.classLoader === classLoader || it.key.holder in removedKeys
        }.sortedBy { it.block.weight }

        for (activeEffect in toDisable) {
            active.remove(activeEffect.key)
            safely(activeEffect, "disable") {
                activeEffect.element.disableActive(dispatcher, activeEffect.enabledWith, activeEffect.identifiers)
            }
        }

        for (provider in removedProviders) {
            answers.remove(provider)
            applied.remove(provider)
            providerCheckedAt.remove(provider)
            dirtyProviders.remove(provider)
            pendingProviders.remove(provider)
        }

        rebuildApplied()
        rebuildSnapshot()
        rebuildProvidedActiveEffects()
    }

    private inline fun safely(activeEffect: ActiveEffect, operation: String, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            plugin.logger.warning(
                "Failed to $operation effect ${activeEffect.element.effect.id} " +
                        "(${activeEffect.key.holder.holderId}) for $uuid"
            )
            e.printStackTrace()
        } catch (e: LinkageError) {
            plugin.logger.warning(
                "Failed to $operation effect ${activeEffect.element.effect.id} " +
                        "(${activeEffect.key.holder.holderId}) for $uuid: ${e.message}"
            )
        }
    }
}

private val ProvidedHolder.slotType
    get() = (this as? SlotItemProvidedHolder<*>)?.slotType
