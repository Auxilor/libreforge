package com.willfp.libreforge

import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.ConditionBlock
import com.willfp.libreforge.effects.ChainElement
import com.willfp.libreforge.effects.EffectBlock
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.ProviderBinding
import com.willfp.libreforge.slot.SlotItemProvidedHolder
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import java.util.Collections
import java.util.IdentityHashMap
import java.util.Objects
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
) {
    private val hash = Objects.hash(providerId, holderId, occurrence)

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean =
        this === other || other is HolderKey && hash == other.hash && occurrence == other.occurrence
                && holderId == other.holderId && providerId == other.providerId
}

/**
 * Identity of one enabled effect element.
 */
internal data class EffectKey(
    val holder: HolderKey,
    val blockIndex: Int,
    val elementIndex: Int
) {
    /**
     * The identifier discriminator on [dispatcher]. Includes the dispatcher, so effects that key
     * shared state by their identifiers never collide between dispatchers with the same holder.
     */
    fun discriminator(dispatcher: UUID): String =
        "$dispatcher|${holder.providerId}|${holder.holderId}|$blockIndex|$elementIndex|${holder.occurrence}"
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

    // Per effect block, which elements are dynamic; null for blocks that are not permanent.
    private val dynamicElements: Array<BooleanArray?> = providedHolder.holder.effects.map { block ->
        if (block.isPermanent) {
            BooleanArray(block.effects.size) { DynamicConfigs.isDynamic(block.effects[it].config, placeholderNames) }
        } else {
            null
        }
    }.toTypedArray()

    private val hasDynamicEffect = dynamicElements.any { flags -> flags != null && flags.any { it } }

    private val polledConditions = IdentityHashMap<ConditionBlock<*>, Boolean>()

    private var conditionTypes: Set<Condition<*>>? = null

    private var isPolled: Boolean? = null

    /**
     * If the effect element at [blockIndex] and [elementIndex] of a permanent block must be reloaded
     * on every condition pass for this holder.
     */
    fun isDynamic(blockIndex: Int, elementIndex: Int): Boolean =
        dynamicElements.getOrNull(blockIndex)?.getOrNull(elementIndex) == true

    /**
     * As [isDynamic] by index, for an [element] that may be from an earlier compile of the holder.
     */
    fun isDynamic(key: EffectKey, element: ChainElement<*>, holder: Holder): Boolean {
        val block = holder.effects.getOrNull(key.blockIndex)
        if (block != null && block.effects.getOrNull(key.elementIndex) === element) {
            return isDynamic(key.blockIndex, key.elementIndex)
        }

        return DynamicConfigs.isDynamic(element.config, placeholderNames)
    }

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

        return (hasPolledCondition || hasDynamicEffect).also { isPolled = it }
    }

    /**
     * If [holder] uses any of [conditions], at holder or effect level.
     */
    fun usesAny(holder: ProvidedHolder, conditions: Set<Condition<*>>, scope: SignalScope?): Boolean {
        val types = conditionTypes ?: buildSet {
            holder.holder.conditions.forEach { add(it.condition) }
            holder.holder.effects.forEach { block -> block.conditions.forEach { add(it.condition) } }
        }.also { conditionTypes = it }

        return types.any { it in conditions && (scope == null || it.isInvalidatedBy(scope, holder)) }
    }
}

/**
 * The keys of the re-asked providers, classified against what was applied.
 */
internal class HolderDiff(
    val holdersChanged: Boolean
) : KeyChanges<HolderKey, ProvidedHolder>()

/**
 * Which effect blocks of a provided holder are met.
 */
internal class Evaluation(
    val key: HolderKey,
    val holder: ProvidedHolder,
    val met: BooleanArray
)

/**
 * An active effect to reload with the [current] provided holder.
 */
internal class Reload(
    val activeEffect: ActiveEffect,
    val current: ProvidedHolder
)

/**
 * The effects one update disables, enables and reloads.
 */
internal class EffectDelta {
    val disables = ArrayList<ActiveEffect>()
    val enables = ArrayList<ActiveEffect>()
    val reloads = LinkedHashMap<EffectKey, Reload>()
}

internal enum class StateKind {
    PLAYER,
    GLOBAL,
    ENTITY,
    CUSTOM
}

/**
 * Everything libreforge tracks for one dispatcher. Accessed only on the thread that owns the
 * dispatcher (the main thread off Folia); [admitted] and [isRemoved] are also read by the engine.
 */
internal class HolderState(
    val dispatcher: Dispatcher<*>,
    val kind: StateKind
) {
    val uuid: UUID = dispatcher.uuid

    // Each provider's last answer, keyed. A provider with no entry has never been asked.
    private val answers = IdentityHashMap<HolderProvider, LinkedHashMap<HolderKey, ProvidedHolder>>()

    // Each scoped provider's last answer as returned, so an answer handed back unchanged skips keying.
    private val rawAnswers = IdentityHashMap<HolderProvider, Collection<ProvidedHolder>>()

    private val applied = IdentityHashMap<HolderProvider, LinkedHashMap<HolderKey, ProvidedHolder>>()

    private var appliedByKey = HashMap<HolderKey, ProvidedHolder>()

    private var appliedOrder = emptyList<Pair<HolderKey, ProvidedHolder>>()

    private var appliedSnapshot = emptyList<ProvidedHolder>()

    // The holders events were last fired for, by id. Kept through a reset, so a reload only fires for
    // holders that really came or went.
    private var announced = emptyMap<NamespacedKey, ProvidedHolder>()

    private var announcedSnapshot = emptyList<ProvidedHolder>()

    private var settlingReset = false

    private val providerCheckedAt = IdentityHashMap<HolderProvider, Int>()

    var conditionsCheckedAt = 0
    var repairedAt = 0
    var reloadedAt = 0
    var lastHolderUpdateAt = 0L
    var lastClickRefreshAt = 0L

    private val dirtyProviders: MutableSet<HolderProvider> = Collections.newSetFromMap(IdentityHashMap())

    // Dirty providers marked only by scoped signals, with those scopes. Absent means full.
    private val providerScopes = IdentityHashMap<HolderProvider, ArrayList<SignalScope>>()

    private val providerMemory = IdentityHashMap<HolderProvider, ProviderMemory>()

    // Answered but not yet applied, e.g. asked by a read before the first flush.
    private val pendingProviders: MutableSet<HolderProvider> = Collections.newSetFromMap(IdentityHashMap())

    val conditionDirtyHolders = HashSet<HolderKey>()
    var conditionDirtyAll = false
    var repairDue = false
    var reloadDue = false
    var reloadAll = false
    var resetPending = false
    var clickPending = false
        private set

    // Scopes of the pending clicks, or null if any click touched everything.
    private var clickScopes: ArrayList<SignalScope>? = ArrayList()

    // Set when the state is dropped, possibly by a handler in the middle of its own update.
    @Volatile
    var isRemoved = false

    var admissionAttempts = 0

    var bypassCooldown = false

    // Non-player states wait for the new-state budget before their first flush.
    @Volatile
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
                || conditionDirtyHolders.isNotEmpty() || repairDue || reloadDue || reloadAll || resetPending || clickPending

    /**
     * If this state holds nothing and has nothing to do, so dropping it changes nothing.
     */
    val isIdle: Boolean
        get() = active.isEmpty() && appliedOrder.isEmpty() && !hasWork

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
        askUnasked(listOf(provider), tick)
        return answers[provider]?.values?.let { Collections.unmodifiableCollection(it) } ?: emptyList()
    }

    /**
     * Ask every provider that has never been asked, as reads must see the right holders.
     */
    fun ensureAsked(tick: Int) =
        askUnasked(registeredHolderProviders, tick)

    private fun askUnasked(providers: List<HolderProvider>, tick: Int) {
        var asked = false
        val pass = ProvidePass()

        for (provider in providers) {
            if (!answers.containsKey(provider)) {
                ask(provider, tick, pass)
                dirtyProviders.remove(provider)
                asked = true
            }
        }

        if (asked) {
            rebuildSnapshot()
            HolderStates.markDirty(this)
        }
    }

    /**
     * If the last answer of [provider] contains [holder].
     */
    fun provides(provider: HolderProvider, holder: Holder): Boolean =
        answers[provider]?.values?.any { it.holder === holder } == true

    fun answersByProvider(): Map<HolderProvider, List<ProvidedHolder>> {
        val map = IdentityHashMap<HolderProvider, List<ProvidedHolder>>()
        for ((provider, keyed) in answers) {
            map[provider] = keyed.values.toList()
        }
        return map
    }

    /**
     * Ask [provider] again. Returns true if its answer changed, so it must be applied.
     */
    private fun ask(provider: HolderProvider, tick: Int, pass: ProvidePass): Boolean {
        val scopes = providerScopes.remove(provider)

        // A scoped ask only re-checks part of the provider, so only a full ask resets its polling age.
        if (scopes == null || provider !is ScopedHolderProvider) {
            providerCheckedAt[provider] = tick
        }

        if (provider !is ScopedHolderProvider) {
            return applyAnswer(provider, provider.ask(dispatcher))
        }

        val answer = provider.provide(
            ProvideContext(
                dispatcher,
                if (scopes == null) SignalScopes.FULL else SignalScopes(scopes),
                providerMemory.getOrPut(provider) { ProviderMemory() },
                pass
            )
        )

        if (answer === rawAnswers[provider] && answers.containsKey(provider)) {
            return false
        }

        rawAnswers[provider] = answer
        return applyAnswer(provider, answer)
    }

    /**
     * Key and store [answer] as the answer of [provider]. Returns true if it changed.
     */
    private fun applyAnswer(provider: HolderProvider, answer: Collection<ProvidedHolder>): Boolean {
        val providerId = registeredProviderId(provider)
        val occurrences = HashMap<NamespacedKey, Int>()
        val keyed = LinkedHashMap<HolderKey, ProvidedHolder>()

        for (ph in answer) {
            val holderId = ph.holder.id
            val occurrence = occurrences.getOrDefault(holderId, 0)
            occurrences[holderId] = occurrence + 1
            keyed[HolderKey(providerId, holderId, occurrence)] = ph
        }

        val previous = answers[provider]
        if (previous != null && isSameAnswer(previous, keyed) { it.slotType }) {
            return false
        }

        answers[provider] = keyed
        pendingProviders += provider
        return true
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
    fun visit(tick: Int, settings: HolderStates.Settings) {
        for (provider in registeredHolderProviders) {
            val checkedAt = providerCheckedAt[provider] ?: continue
            val maxAge = provider.maxAge(dispatcher) ?: continue
            if (tick - checkedAt >= maxAge) {
                markProvider(provider)
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

        if (tick - repairedAt >= settings.repairInterval) {
            repairDue = true
        }

        if (tick - reloadedAt >= settings.reloadInterval) {
            reloadDue = true
        }
    }

    /**
     * Mark [provider] to be re-asked for everything.
     */
    fun markProvider(provider: HolderProvider) {
        dirtyProviders += provider
        providerScopes.remove(provider)
    }

    fun markProviders(providers: Collection<HolderProvider>) =
        providers.forEach { markProvider(it) }

    /**
     * Mark [provider] to be re-asked for [scope], or for everything if null. A provider already
     * marked for everything stays so.
     */
    fun markProvider(provider: HolderProvider, scope: SignalScope?) {
        if (scope == null) {
            markProvider(provider)
            return
        }

        if (dirtyProviders.add(provider)) {
            providerScopes[provider] = arrayListOf(scope)
            return
        }

        val scopes = providerScopes[provider] ?: return
        if (scopes.size >= MAX_PENDING_SCOPES) {
            providerScopes.remove(provider)
        } else {
            scopes += scope
        }
    }

    /**
     * Mark the providers and conditions that a built-in [change] invalidates, touching [scope].
     */
    fun applySignal(change: HolderChange, scope: SignalScope? = null) {
        for (provider in HolderSignals.providersFor(change)) {
            markProvider(provider, scope)
        }

        markConditionHolders(HolderSignals.conditionsFor(change), scope)
    }

    fun markConditionHolders(conditions: Set<Condition<*>>, scope: SignalScope? = null) {
        if (conditions.isEmpty()) {
            return
        }

        for ((key, ph) in appliedOrder) {
            if (dataFor(ph).usesAny(ph, conditions, scope)) {
                conditionDirtyHolders += key
            }
        }
    }

    /**
     * Queue an inventory click touching [scope], or everything if null.
     */
    fun addPendingClick(scope: SignalScope?) {
        clickPending = true

        val scopes = clickScopes ?: return
        if (scope == null || scopes.size >= MAX_PENDING_SCOPES) {
            clickScopes = null
        } else {
            scopes += scope
        }
    }

    /**
     * Apply everything marked. Returns true if some work was held back by rate limiting.
     */
    fun update(tick: Int, now: Long, settings: HolderStates.Settings): Boolean {
        if (resetPending) {
            reset()
        }

        val clickHeldBack = applyPendingClick(now, settings)
        val askHeldBack = reaskDirtyProviders(tick, now, settings)

        val diff = classifyPendingProviders()
        fireHolderEvents(diff)

        val fullConditionPass = conditionDirtyAll
        val toEvaluate = takeHoldersToEvaluate(diff)

        // A handler may have removed this state (e.g. kicked the player); its effects are already disabled.
        if (isRemoved) {
            return false
        }

        val delta = effectDelta(diff, evaluateAll(toEvaluate))

        if (!applyDelta(delta)) {
            return false
        }

        if (delta.disables.isNotEmpty() || delta.enables.isNotEmpty() || diff.holdersChanged) {
            rebuildProvidedActiveEffects()
        }

        runSlowPasses(tick)

        // Only a full pass stamps; periodic marks are stamped when made (visit), so a frequent partial
        // signal never starves the periodic check.
        if (fullConditionPass) {
            conditionsCheckedAt = tick
        }

        return clickHeldBack || askHeldBack
    }

    /**
     * Turn a pending inventory click into an item signal, unless rate limited. Returns true if held back.
     */
    private fun applyPendingClick(now: Long, settings: HolderStates.Settings): Boolean {
        if (!clickPending) {
            return false
        }

        if (lastClickRefreshAt != 0L && now - lastClickRefreshAt < settings.inventoryClickTimeout) {
            return true
        }

        clickPending = false
        lastClickRefreshAt = now

        val scopes = clickScopes
        clickScopes = ArrayList()

        if (scopes == null) {
            applySignal(HolderChange.Items)
        } else {
            scopes.forEach { applySignal(HolderChange.Items, it) }
        }

        return false
    }

    /**
     * Re-ask the dirty providers, unless delayed by `refresh.cooldown`. Returns true if held back.
     */
    private fun reaskDirtyProviders(tick: Int, now: Long, settings: HolderStates.Settings): Boolean {
        if (dirtyProviders.isEmpty()) {
            return false
        }

        if (!bypassCooldown && lastHolderUpdateAt != 0L && now - lastHolderUpdateAt < settings.cooldown) {
            return true
        }

        bypassCooldown = false
        dispatcher.runRefreshFunctions()

        var changed = false
        val pass = ProvidePass()
        for (provider in registeredHolderProviders) {
            if (provider in dirtyProviders && ask(provider, tick, pass)) {
                changed = true
            }
        }

        dirtyProviders.clear()
        providerScopes.clear()
        lastHolderUpdateAt = now

        if (changed) {
            rebuildSnapshot()
        }

        return false
    }

    /**
     * Apply the answers of the re-asked providers, classifying each key against what was applied.
     */
    private fun classifyPendingProviders(): HolderDiff {
        val diff = HolderDiff(pendingProviders.isNotEmpty())

        if (!diff.holdersChanged) {
            return diff
        }

        for (provider in pendingProviders) {
            val old = applied[provider] ?: emptyMap()
            val new = answers[provider] ?: LinkedHashMap()

            diff.classify(old, new, { it.holder }, { it.slotType })

            if (new.isEmpty()) {
                applied.remove(provider)
            } else {
                applied[provider] = new
            }
        }

        pendingProviders.clear()
        rebuildApplied()
        return diff
    }

    /**
     * Fire holder events per holder id: enable when an id is first provided, disable when its last
     * copy goes. During a reset, disables wait until every provider has been asked again.
     */
    private fun fireHolderEvents(diff: HolderDiff) {
        val settled = settlingReset && dirtyProviders.isEmpty()
        if (!diff.holdersChanged && !settled) {
            return
        }

        val current = LinkedHashMap<NamespacedKey, ProvidedHolder>()
        for (ph in appliedSnapshot) {
            current.putIfAbsent(ph.holder.id, ph)
        }

        val enabled = current.filterKeys { it !in announced }
        val disabled = if (settlingReset && !settled) emptyMap() else announced.filterKeys { it !in current }
        val before = announcedSnapshot
        val after = appliedSnapshot

        if (settlingReset && !settled) {
            announced = announced + enabled
        } else {
            settlingReset = false
            announced = current
            announcedSnapshot = after
        }

        if (enabled.isEmpty() && disabled.isEmpty()) {
            return
        }

        for (ph in enabled.values) {
            Bukkit.getPluginManager().callEvent(HolderEnableEvent(dispatcher, ph, after))
        }

        for (ph in disabled.values) {
            Bukkit.getPluginManager().callEvent(HolderDisableEvent(dispatcher, ph, before))
        }

        @Suppress("DEPRECATION")
        Bukkit.getPluginManager().callEvent(HolderProvideEvent(dispatcher, after))
    }

    /**
     * The holders whose conditions must be evaluated: new, moved, and condition-dirty ones.
     */
    private fun takeHoldersToEvaluate(diff: HolderDiff): Map<HolderKey, ProvidedHolder> {
        val toEvaluate = LinkedHashMap<HolderKey, ProvidedHolder>()

        for ((key, ph) in diff.added) {
            toEvaluate[key] = ph
        }

        for (key in diff.moved) {
            appliedByKey[key]?.let { toEvaluate[key] = it }
        }

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
        return toEvaluate
    }

    /**
     * Evaluate everything before changing anything, so a throwing condition leaves the holder as it was.
     */
    private fun evaluateAll(toEvaluate: Map<HolderKey, ProvidedHolder>): List<Evaluation> {
        val evaluations = ArrayList<Evaluation>(toEvaluate.size)

        for ((key, ph) in toEvaluate) {
            val met = try {
                ph.metBlocks(dispatcher)
            } catch (e: Exception) {
                plugin.logger.warning("Failed to evaluate conditions of ${ph.holder.id} for $uuid")
                e.printStackTrace()
                continue
            }

            evaluations += Evaluation(key, ph, met)
        }

        return evaluations
    }

    /**
     * Work out which effects to disable, enable and reload, removing the disabled ones from active.
     */
    private fun effectDelta(diff: HolderDiff, evaluations: List<Evaluation>): EffectDelta {
        val delta = EffectDelta()

        if (diff.removed.isNotEmpty()) {
            val removedKeys = diff.removed.mapTo(HashSet()) { it.first }
            val iterator = active.values.iterator()
            while (iterator.hasNext()) {
                val activeEffect = iterator.next()
                if (activeEffect.key.holder in removedKeys) {
                    delta.disables += activeEffect
                    iterator.remove()
                }
            }
        }

        for (evaluation in evaluations) {
            val key = evaluation.key
            val ph = evaluation.holder
            val data = dataFor(ph)

            val effects = ph.holder.effects
            for (blockIndex in 0 until effects.size) {
                val block = effects[blockIndex]
                val elements = block.effects
                for (elementIndex in 0 until elements.size) {
                    val element = elements[elementIndex]
                    val effectKey = EffectKey(key, blockIndex, elementIndex)
                    val current = active[effectKey]
                    val isMet = evaluation.met[blockIndex] && !HolderStates.isUnloaded(element.effect)

                    if (isMet && current == null) {
                        // Added to active only once enabled, so a removal mid-update never disables it unenabled.
                        delta.enables += ActiveEffect(
                            effectKey,
                            block,
                            element,
                            element.effect.makeIdentifiers(effectKey.discriminator(uuid)),
                            ph
                        )
                    } else if (!isMet && current != null) {
                        active.remove(effectKey)
                        delta.disables += current
                    } else if (current != null && block.isPermanent) {
                        val rebind = key in diff.moved && element.effect.providerBinding != ProviderBinding.NONE
                        if (rebind || data.isDynamic(blockIndex, elementIndex)) {
                            delta.reloads[effectKey] = Reload(current, ph)
                        }
                    }
                }
            }
        }

        if (diff.holdersChanged) {
            addItemRebinds(delta)
        }

        return delta
    }

    /**
     * Reload effects acting on the item of unchanged holders whose item changed.
     */
    private fun addItemRebinds(delta: EffectDelta) {
        for (activeEffect in active.values) {
            if (!activeEffect.isPermanent || activeEffect.element.effect.providerBinding != ProviderBinding.ITEM) {
                continue
            }

            if (activeEffect.key in delta.reloads) {
                continue
            }

            val current = appliedByKey[activeEffect.key.holder] ?: continue
            if (current.getProvider<ItemStack>() != activeEffect.enabledWith.getProvider<ItemStack>()) {
                delta.reloads[activeEffect.key] = Reload(activeEffect, current)
            }
        }
    }

    /**
     * Disable, then enable, then reload. Returns false if the state was removed meanwhile.
     */
    private fun applyDelta(delta: EffectDelta): Boolean {
        // Disables always run, even if the state is removed meanwhile: these are no longer in active.
        for (activeEffect in delta.disables.sortedBy { it.block.weight }) {
            disable(activeEffect)
        }

        for (activeEffect in delta.enables.sortedBy { it.block.weight }) {
            if (isRemoved) {
                return false
            }

            safely(activeEffect, "enable") {
                activeEffect.element.enableActive(dispatcher, activeEffect.enabledWith, activeEffect.identifiers)
            }

            // Removed by a handler inside the enable: disableAll has already run without it.
            if (isRemoved) {
                disable(activeEffect)
                return false
            }

            active[activeEffect.key] = activeEffect
        }

        for (pending in delta.reloads.values.sortedBy { it.activeEffect.block.weight }) {
            if (isRemoved) {
                return false
            }

            reload(pending.activeEffect, pending.current)
        }

        return !isRemoved
    }

    private fun runSlowPasses(tick: Int) {
        if (reloadAll) {
            reloadAll = false
            repairDue = false
            reloadDue = false
            repairedAt = tick
            reloadedAt = tick
            reloadAllPermanent()
        } else if (repairDue || reloadDue) {
            val blind = reloadDue
            repairDue = false
            reloadDue = false
            repairedAt = tick

            if (blind) {
                reloadedAt = tick
            }

            repair(blind)
        }
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

    /**
     * Re-apply static permanent effects whose applied state is gone, and if [blind], reload those
     * that can't tell. Dynamic ones are already reloaded on every condition pass.
     */
    private fun repair(blind: Boolean) {
        var missing: ArrayList<ActiveEffect>? = null
        var unknown: ArrayList<ActiveEffect>? = null

        for (activeEffect in active.values) {
            if (!activeEffect.isPermanent || HolderStates.isUnloaded(activeEffect.element.effect)) {
                continue
            }

            val current = appliedByKey[activeEffect.key.holder] ?: continue

            if (dataFor(current).isDynamic(activeEffect.key, activeEffect.element, current.holder)) {
                continue
            }

            when (isApplied(activeEffect)) {
                true -> Unit
                false -> (missing ?: ArrayList<ActiveEffect>().also { missing = it }) += activeEffect
                null -> if (blind) {
                    (unknown ?: ArrayList<ActiveEffect>().also { unknown = it }) += activeEffect
                }
            }
        }

        missing?.sortedBy { it.block.weight }?.forEach { activeEffect ->
            val current = appliedByKey[activeEffect.key.holder] ?: return@forEach
            reapply(activeEffect, current)
        }

        unknown?.sortedBy { it.block.weight }?.forEach { activeEffect ->
            val current = appliedByKey[activeEffect.key.holder] ?: return@forEach
            reload(activeEffect, current)
        }
    }

    // A throwing check counts as applied, so a broken effect is not re-applied on every pass.
    private fun isApplied(activeEffect: ActiveEffect): Boolean? {
        var applied: Boolean? = true

        safely(activeEffect, "check") {
            applied = activeEffect.element.effect.isApplied(dispatcher, activeEffect.identifiers, activeEffect.enabledWith)
        }

        return applied
    }

    private fun reapply(activeEffect: ActiveEffect, current: ProvidedHolder) {
        safely(activeEffect, "re-apply") {
            activeEffect.element.reapplyActive(dispatcher, activeEffect.enabledWith, current, activeEffect.identifiers)
            activeEffect.enabledWith = current
        }
    }

    /**
     * Reload every permanent effect.
     */
    private fun reloadAllPermanent() {
        for (activeEffect in permanentEffectsByWeight()) {
            val current = appliedByKey[activeEffect.key.holder] ?: continue
            reload(activeEffect, current)
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
        settlingReset = true
        disableAll()

        answers.clear()
        rawAnswers.clear()
        applied.clear()
        pendingProviders.clear()
        providerCheckedAt.clear()
        providerMemory.clear()
        holderData.clear()
        rebuildApplied()
        rebuildSnapshot()

        conditionDirtyAll = false
        conditionDirtyHolders.clear()
        markProviders(registeredHolderProviders)
        bypassCooldown = true
        HolderStates.clearConditionResults(uuid)
    }

    /**
     * Disable every active effect with the provided holder it was enabled with.
     */
    fun disableAll() {
        val toDisable = active.values.sortedBy { it.block.weight }
        active.clear()

        for (activeEffect in toDisable) {
            disable(activeEffect)
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
            disable(activeEffect)
        }

        for (provider in removedProviders) {
            answers.remove(provider)
            rawAnswers.remove(provider)
            applied.remove(provider)
            providerCheckedAt.remove(provider)
            providerMemory.remove(provider)
            providerScopes.remove(provider)
            dirtyProviders.remove(provider)
            pendingProviders.remove(provider)
        }

        rebuildApplied()
        rebuildSnapshot()
        rebuildProvidedActiveEffects()
    }

    private fun disable(activeEffect: ActiveEffect) {
        safely(activeEffect, "disable") {
            activeEffect.element.disableActive(dispatcher, activeEffect.enabledWith, activeEffect.identifiers)
        }
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

// Beyond this, pending scopes collapse to a full re-ask, which is cheaper than matching them all.
private const val MAX_PENDING_SCOPES = 32

private val ProvidedHolder.slotType
    get() = (this as? SlotItemProvidedHolder<*>)?.slotType
