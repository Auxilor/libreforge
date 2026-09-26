package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.applyHolder
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.mutators.emptyMutatorList
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.event.Listener
import java.util.UUID

/**
 * What a permanent effect's applied state depends on.
 */
enum class ProviderBinding {
    /**
     * Keyed only by [Identifiers]; unaffected by the slot or the item.
     */
    NONE,

    /**
     * Depends on the slot the holder is provided from.
     */
    SLOT,

    /**
     * Acts on the item that provides the holder.
     */
    ITEM
}

/**
 * The result of [Effect.checkIntegrity].
 */
enum class Integrity {
    /**
     * The effect's applied state is present.
     */
    INTACT,

    /**
     * The effect's applied state is gone, so it can safely be applied again.
     */
    MISSING,

    /**
     * The effect cannot tell.
     */
    UNKNOWN
}

abstract class Effect<T>(
    final override val id: String
) : Compilable<T>(), Listener {
    // The identifier factory.
    private val identifierFactory = IdentifierFactory(UUID.nameUUIDFromBytes(id.toByteArray()))

    /**
     * If the effect should be reloaded.
     */
    open val shouldReload = true

    /**
     * What this effect's applied state depends on, deciding what happens when the holder moves slot
     * or its item changes.
     */
    open val providerBinding: ProviderBinding
        get() = ProviderBinding.SLOT

    /**
     * If the effect should be reloaded on every condition pass, for effects whose [onEnable] reads
     * dispatcher or item state directly rather than through placeholders.
     */
    open val alwaysReload: Boolean
        get() = false

    /**
     * The run order.
     */
    open val runOrder = RunOrder.NORMAL

    /**
     * If the effect can be delayed.
     */
    open val supportsDelay = true

    /**
     * The required trigger parameters.
     */
    protected open val parameters: Set<TriggerParameter> = emptySet()

    /**
     * If the effect is permanent.
     */
    open val isPermanent: Boolean
        get() = parameters.isEmpty()

    /**
     * If the effect supports a certain [trigger].
     */
    fun supportsTrigger(trigger: Trigger) =
        Triggers.withParameters(parameters)(trigger, emptyMutatorList())

    /**
     * If the effect supports a certain [trigger] after [mutators] are applied to it.
     */
    fun supportsTrigger(trigger: Trigger, mutators: MutatorList) =
        Triggers.withParameters(parameters)(trigger, mutators)

    private fun discriminator(holder: ProvidedHolder, blockIndex: Int, elementIndex: Int, occurrence: Int): String {
        return "${holder.holder.id}|$blockIndex|$elementIndex|$occurrence"
    }

    /**
     * Enable a permanent effect for a [dispatcher].
     */
    fun enable(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        config: ChainElement<T>,
        blockIndex: Int = 0,
        elementIndex: Int = 0,
        occurrence: Int = 0,
        isReload: Boolean = false
    ) {
        if (isReload && !shouldReload) {
            return
        }

        val withHolder = config.config.applyHolder(holder, dispatcher)

        val identifiers = identifierFactory.makeIdentifiers(discriminator(holder, blockIndex, elementIndex, occurrence))

        onEnable(dispatcher, withHolder, identifiers, holder, config.compileData)
    }

    /**
     * Handle the enabling of this permanent effect.
     */
    protected open fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: T
    ) {
        // Override when needed.
    }

    /**
     * Handle a reload of this permanent effect from the [previous] provided holder to the [current] one.
     *
     * Only called when [shouldReload] is true. Defaults to disabling and re-enabling.
     */
    protected open fun onReload(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        previous: ProvidedHolder,
        current: ProvidedHolder,
        compileData: T
    ) {
        onDisable(dispatcher, identifiers, previous)
        onEnable(dispatcher, config, identifiers, current, compileData)
    }

    /**
     * Check if the applied state of this permanent effect is still present.
     */
    open fun checkIntegrity(
        dispatcher: Dispatcher<*>,
        identifiers: Identifiers,
        holder: ProvidedHolder
    ): Integrity = Integrity.UNKNOWN

    internal fun makeIdentifiers(discriminator: String): Identifiers =
        identifierFactory.makeIdentifiers(discriminator)

    internal fun enableWith(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        element: ChainElement<T>,
        identifiers: Identifiers
    ) {
        onEnable(dispatcher, element.config.applyHolder(holder, dispatcher), identifiers, holder, element.compileData)
    }

    internal fun disableWith(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        identifiers: Identifiers
    ) {
        onDisable(dispatcher, identifiers, holder)
    }

    /**
     * Returns false if skipped because the effect must not be reloaded.
     */
    internal fun reloadWith(
        dispatcher: Dispatcher<*>,
        previous: ProvidedHolder,
        current: ProvidedHolder,
        element: ChainElement<T>,
        identifiers: Identifiers
    ): Boolean {
        if (!shouldReload) {
            return false
        }

        onReload(
            dispatcher,
            element.config.applyHolder(current, dispatcher),
            identifiers,
            previous,
            current,
            element.compileData
        )

        return true
    }

    internal fun repairWith(
        dispatcher: Dispatcher<*>,
        previous: ProvidedHolder,
        current: ProvidedHolder,
        element: ChainElement<T>,
        identifiers: Identifiers
    ) {
        onDisable(dispatcher, identifiers, previous)
        enableWith(dispatcher, current, element, identifiers)
    }

    /**
     * Disable a permanent effect for a [dispatcher].
     */
    fun disable(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        blockIndex: Int = 0,
        elementIndex: Int = 0,
        occurrence: Int = 0,
        isReload: Boolean = false
    ) {
        if (isReload && !shouldReload) {
            return
        }

        val identifiers = identifierFactory.makeIdentifiers(discriminator(holder, blockIndex, elementIndex, occurrence))

        onDisable(dispatcher, identifiers, holder)
    }

    /**
     * Handle the disabling of this permanent effect.
     */
    protected open fun onDisable(
        dispatcher: Dispatcher<*>,
        identifiers: Identifiers,
        holder: ProvidedHolder
    ) {
        // Override when needed.
    }

    /**
     * Trigger the effect.
     *
     * Returns if the execution was successful.
     *
     * @param trigger The trigger.
     * @param config The config.
     */
    fun trigger(
        trigger: DispatchedTrigger,
        config: ChainElement<T>
    ): Boolean = onTrigger(
        config.config,
        trigger.data.copy().apply { this.inheritedTriggerPlaceholders = trigger.rawPlaceholders },
        config.compileData
    )

    /**
     * Handle triggering.
     *
     * Returns if the execution was successful.
     *
     * @param data The trigger data.
     * @param compileData The compile data.
     */
    protected open fun onTrigger(
        config: Config,
        data: TriggerData,
        compileData: T
    ): Boolean {
        return false
    }

    /**
     * If the effect should trigger.
     */
    fun shouldTrigger(
        trigger: DispatchedTrigger,
        config: ChainElement<T>
    ): Boolean = shouldTrigger(config.config, trigger.data, config.compileData)

    /**
     * If the effect should trigger, ran before effect arguments in order
     * to prevent unnecessary calculations.
     */
    protected open fun shouldTrigger(
        config: Config,
        data: TriggerData,
        compileData: T
    ): Boolean {
        return true
    }

    private var isListenerRegistered = false

    final override fun onRegister() {
        if (isListenerRegistered) return
        isListenerRegistered = true

        plugin.runWhenEnabled {
            plugin.eventManager.unregisterListener(this)
            plugin.eventManager.registerListener(this)
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }
}
