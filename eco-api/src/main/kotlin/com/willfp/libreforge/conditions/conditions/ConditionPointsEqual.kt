package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.getPoints
import org.bukkit.entity.Player

class ConditionPointsEqual : Condition("points_equal") {
    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("amount", "You must specify the amount of points!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.getPoints(config.getString("type")) == config.getDoubleFromExpression("amount", player)
    }
}
