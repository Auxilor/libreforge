package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsExpressionTrue : Condition("is_expression_true") {
    override val arguments = arguments {
        require("expression", "You must specify the expression!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return config.getDoubleFromExpression("expression", player) == 1.0
    }
}
