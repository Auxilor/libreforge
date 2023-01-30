package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInWater : Condition("in_water") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.isInWater == (config.getBoolOrNull("in_water") ?: true)
    }
}