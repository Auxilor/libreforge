package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.defaultMap
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.applyHolder
import com.willfp.libreforge.plugin
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
    // Maps Player UUIDs to the effect count.
    private val effectCounter = defaultMap<UUID, Int>(0)

    // The identifier factory.
    private val identifierFactory = IdentifierFactory(UUID.nameUUIDFromBytes(id.toByteArray()))

    /**
     * If the effect should be reloaded.
     */
    open val shouldReload = true

    /**
     * If the effect should disable during reload.
     *
     * For example, Attribute-based effects have onEnable logic that does not require onDisable
     * to be run to prevent a collision / duplication. For these effects, this should be false.
     */
    @Deprecated("This caused issues with effects and is no longer used.", ReplaceWith("true"), DeprecationLevel.ERROR)
    open val disablesDuringReload = true

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
        Triggers.withParameters(parameters)(trigger)

    /**
     * Enable a permanent effect.
     *
     * @param player The player.
     * @param config The effect config.
     */
    fun enable(
        player: Player,
        holder: ProvidedHolder,
        config: ChainElement<T>,
        isReload: Boolean = false
    ) {
        if (isReload && !shouldReload) {
            return
        }

        // Increment first to fix reload bug where effects are applied twice.
        effectCounter[player.uniqueId]++
        val count = effectCounter[player.uniqueId]

        val withHolder = config.config.applyHolder(holder)

        onEnable(player, withHolder, identifierFactory.makeIdentifiers(count), holder, config.compileData)
    }

    /**
     * Enable a permanent effect.
     *
     * @param player The player.
     * @param identifierFactory The identifier factory.
     * @param config The effect config.
     */
    @Deprecated(
        "Use enable(player, holder, config) instead.",
        ReplaceWith("enable(player, holder, config)"),
        DeprecationLevel.ERROR
    )
    @Suppress("UNUSED_PARAMETER")
    fun enable(
        player: Player,
        identifierFactory: IdentifierFactory,
        holder: ProvidedHolder,
        config: ChainElement<T>
    ) {
        enable(player, holder, config)
    }

    /**
     * Handle the enabling of this permanent effect.
     *
     * @param player The player.
     * @param config The config.
     * @param identifiers The identifiers.
     * @param compileData The compile data.
     */
    protected open fun onEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: T
    ) {
        // Override when needed.
    }

    /**
     * Disable a permanent effect.
     *
     * @param player The player.
     */
    fun disable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) {
        if (isReload && !shouldReload) {
            return
        }

        if (effectCounter[player.uniqueId] == 0) {
            return
        }

        val count = effectCounter[player.uniqueId]--
        onDisable(player, identifierFactory.makeIdentifiers(count), holder)
    }

    /**
     * Disable a permanent effect.
     *
     * @param player The player.
     * @param identifierFactory The identifier factory.
     */
    @Deprecated(
        "Use disable(player, holder) instead.",
        ReplaceWith("disable(player, holder)"),
        DeprecationLevel.ERROR
    )
    @Suppress("UNUSED_PARAMETER")
    fun disable(
        player: Player,
        identifierFactory: IdentifierFactory,
        holder: ProvidedHolder
    ) {
        disable(player, holder)
    }

    /**
     * Handle the disabling of this permanent effect.
     *
     * @param player The player.
     * @param identifiers The identifiers.
     */
    protected open fun onDisable(
        player: Player,
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

    /**
     * Reload the effect.
     *
     * @param player The player.
     * @param identifierFactory The identifier factory.
     * @param config The effect config.
     */
    @Deprecated(
        "Reloading is now handled by EffectBlock.",
        ReplaceWith("effectBlock.reload(player, holder)"),
        DeprecationLevel.ERROR
    )
    @Suppress("UNUSED_PARAMETER")
    fun reload(
        player: Player,
        identifierFactory: IdentifierFactory,
        holder: ProvidedHolder,
        config: ChainElement<T>
    ) {
        // Do nothing.
    }

    final override fun onRegister() {
        plugin.runWhenEnabled {
            plugin.eventManager.registerListener(this)
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }
}
