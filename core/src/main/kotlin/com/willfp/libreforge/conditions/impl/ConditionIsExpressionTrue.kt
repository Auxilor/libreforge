package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionIsExpressionTrue : Condition<NoCompileData>("is_expression_true") {
    override val arguments = arguments {
        require("expression", "You must specify the expression!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return config.getDoubleFromExpression("expression", player) == 1.0
    }
}
