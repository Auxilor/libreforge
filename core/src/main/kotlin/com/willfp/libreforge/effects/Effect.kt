package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.defaultMap
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.applyHolder
import com.willfp.libreforge.generatePlaceholders
import com.willfp.libreforge.ifType
import com.willfp.libreforge.mapToPlaceholders
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.mutators.emptyMutatorList
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.UUID

abstract class Effect<T>(
    final override val id: String
) : Compilable<T>(), Listener {
    // Maps Dispatcher UUIDs to the effect count.
    private val effectCounter = defaultMap<UUID, Int>(0)

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

    /**
     * Enable a permanent effect for a [dispatcher].
     */
    fun enable(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        config: ChainElement<T>,
        isReload: Boolean = false
    ) {
        if (isReload && !shouldReload) {
            return
        }

        // Increment first to fix reload bug where effects are applied twice.
        effectCounter[dispatcher.uuid]++
        val count = effectCounter[dispatcher.uuid]

        val withHolder = config.config.applyHolder(holder, dispatcher)

        onEnable(dispatcher, withHolder, identifierFactory.makeIdentifiers(count), holder, config.compileData)

        // Legacy support
        dispatcher.ifType<Player> {
            @Suppress("DEPRECATION")
            onEnable(it, withHolder, identifierFactory.makeIdentifiers(count), holder, config.compileData)
        }
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
        isReload: Boolean = false
    ) {
        if (isReload && !shouldReload) {
            return
        }

        if (effectCounter[dispatcher.uuid] == 0) {
            return
        }

        val count = effectCounter[dispatcher.uuid]--

        onDisable(dispatcher, identifierFactory.makeIdentifiers(count), holder)

        // Legacy support
        dispatcher.ifType<Player> {
            @Suppress("DEPRECATION")
            onDisable(it, identifierFactory.makeIdentifiers(count), holder)
        }
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
    ): Boolean = onTrigger(config.config, trigger.data, config.compileData)

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

    final override fun onRegister() {
        plugin.runWhenEnabled {
            plugin.eventManager.unregisterListener(this)
            plugin.eventManager.registerListener(this)
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }



    @Deprecated(
        "Use enable(Dispatcher<*>, ProvidedHolder, ChainElement<T>, Boolean)",
        ReplaceWith("enable(player.toDispatcher(), holder, config, isReload)"),
        DeprecationLevel.ERROR
    )
    fun enable(
        player: Player,
        holder: ProvidedHolder,
        config: ChainElement<T>,
        isReload: Boolean = false
    ): Unit = enable(player.toDispatcher(), holder, config, isReload)

    @Deprecated(
        "Use enable(Dispatcher<*>, ProvidedHolder, ChainElement<T>, Boolean)",
        ReplaceWith("enable(player.toDispatcher(), holder, config, isReload)")
    )
    protected open fun onEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: T
    ) {
        // Override when needed.
    }

    @Deprecated(
        "Use disable(Dispatcher<*>, ProvidedHolder, Boolean)",
        ReplaceWith("disable(player.toDispatcher(), holder, isReload)"),
        DeprecationLevel.ERROR
    )
    fun disable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ): Unit = disable(player.toDispatcher(), holder, isReload)

    @Deprecated(
        "Use disable(Dispatcher<*>, ProvidedHolder, Boolean)",
        ReplaceWith("disable(player.toDispatcher(), holder, isReload)")
    )
    protected open fun onDisable(
        player: Player,
        identifiers: Identifiers,
        holder: ProvidedHolder
    ) {
        // Override when needed.
    }
}
