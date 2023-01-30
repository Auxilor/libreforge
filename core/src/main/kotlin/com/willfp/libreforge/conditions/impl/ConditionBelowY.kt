package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionBelowY : Condition("below_y") {
    override val arguments = arguments {
        require("y", "You must specify the y level!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.location.y < config.getDoubleFromExpression("y", player)
    }
}
