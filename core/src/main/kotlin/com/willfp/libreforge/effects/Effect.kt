package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.DefaultHashMap
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import java.util.UUID

// Done this way so identity equality check works for isPermanent.
private val noTriggers: (Trigger) -> Boolean = { false }

abstract class Effect<T>(
    override val id: String
) : Compilable<T>() {
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
    protected open val parameters: (Trigger) -> Boolean = noTriggers

    /**
     * If the effect is permanent.
     */
    val isPermanent: Boolean
        get() = parameters === noTriggers

    /**
     * If the effect supports a certain [trigger].
     */
    fun supportsTrigger(trigger: Trigger) =
        parameters(trigger)

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
     * @param trigger The trigger.
     * @param config The config.
     */
    fun trigger(
        trigger: DispatchedTrigger,
        config: ChainElement<T>
    ) {
        onTrigger(trigger.player, config.config, trigger.data, config.compileData)
    }

    /**
     * Handle triggering.
     *
     * @param player The player.
     * @param data The trigger data.
     * @param compileData The compile data.
     */
    protected open fun onTrigger(
        player: Player,
        config: Config,
        data: TriggerData,
        compileData: T
    ) {
        // Override when needed.
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
