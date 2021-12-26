package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty
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
    abstract fun isConditionMet(
        player: Player,
        config: Config
    ): Boolean
}

data class ConfiguredCondition(val condition: Condition, val config: Config)
