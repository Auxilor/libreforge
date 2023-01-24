package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionAboveY : Condition("above_y") {
    override val arguments = arguments {
        require("y", "You must specify the y level!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.location.y >= config.getDoubleFromExpression("y", player)
    }
}
