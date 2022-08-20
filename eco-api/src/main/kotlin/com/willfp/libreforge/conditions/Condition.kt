package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty
import com.willfp.libreforge.Holder
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class Condition(
    id: String
) : ConfigurableProperty(id), Listener {
    init {
        postInit()
    }

    private fun postInit() {
        Conditions.addNewCondition(this)
    }

    /**
     * Get if condition is met for a player.
     *
     * @param player The player.
     * @param config The config of the condition.
     * @return If met.
     */
    open fun isConditionMet(
        player: Player,
        config: Config
    ): Boolean = true

    /**
     * Get if condition is met for a player.
     *
     * @param player The player.
     * @param config The config of the condition.
     * @return If met.
     */
    open fun isConditionMet(
        player: Player,
        config: Config,
        data: CompileData?
    ): Boolean = isConditionMet(player, config)

    open fun makeCompileData(config: Config, context: String): CompileData? {
        return null
    }
}
