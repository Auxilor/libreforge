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
