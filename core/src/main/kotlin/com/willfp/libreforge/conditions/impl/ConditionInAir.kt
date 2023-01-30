package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInAir : Condition("in_air") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.location.block.isEmpty == (config.getBoolOrNull("in_air") ?: true)
    }
}