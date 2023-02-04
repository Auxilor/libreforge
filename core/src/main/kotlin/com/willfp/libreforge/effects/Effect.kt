package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.DefaultHashMap
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.UUID

abstract class Effect<T>(
    override val id: String
) : Compilable<T>(), Listener {
    // Maps Player UUIDs to the effect count.
    private val effectCounter = DefaultHashMap<UUID, Int>(0)

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
        Triggers.withParameters(parameters)(trigger)

    /**
     * Enable a permanent effect.
     *
     * @param player The player.
     * @param identifierFactory The identifier factory.
     * @param config The effect config.
     */
    fun enable(
        player: Player,
        identifierFactory: IdentifierFactory,
        config: ChainElement<T>
    ) {
        val count = effectCounter[player.uniqueId]++
        onEnable(player, config.config, identifierFactory.makeIdentifiers(count), config.compileData)
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
        compileData: T
    ) {
        // Override when needed.
    }

    /**
     * Disable a permanent effect.
     *
     * @param player The player.
     * @param identifierFactory The identifier factory.
     */
    fun disable(
        player: Player,
        identifierFactory: IdentifierFactory
    ) {
        val count = effectCounter[player.uniqueId]--
        onDisable(player, identifierFactory.makeIdentifiers(count))
    }

    /**
     * Handle the disabling of this permanent effect.
     *
     * @param player The player.
     * @param identifiers The identifiers.
     */
    protected open fun onDisable(
        player: Player,
        identifiers: Identifiers
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
     * Reload the effect.
     *
     * @param player The player.
     * @param identifierFactory The identifier factory.
     * @param config The effect config.
     */
    fun reload(
        player: Player,
        identifierFactory: IdentifierFactory,
        config: ChainElement<T>
    ) {
        if (!shouldReload) {
            return
        }

        val count = effectCounter[player.uniqueId]
        onDisable(player, identifierFactory.makeIdentifiers(count))
        onEnable(player, config.config, identifierFactory.makeIdentifiers(count), config.compileData)
    }
}
