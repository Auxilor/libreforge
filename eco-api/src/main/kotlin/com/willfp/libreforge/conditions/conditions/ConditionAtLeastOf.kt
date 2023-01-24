package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.CompileData
import org.bukkit.entity.Player

class ConditionAtLeastOf : Condition("at_least_of") {
    override val arguments = arguments {
        require("conditions", "You must specify the conditions that can be met!")
        require("amount", "You must specify the minimum amount of conditions to meet!")
    }

    override fun isConditionMet(player: Player, config: Config, data: CompileData?): Boolean {
        val anyOfData = data as? AtLeastOfCompileData ?: return true

        return anyOfData.isMet(player, config.getIntFromExpression("amount", player))
    }

    override fun makeCompileData(config: Config, context: String): CompileData {
        return AtLeastOfCompileData(
            Conditions.compile(
                config.getSubsections("conditions"),
                "$context -> at_least_of Conditions)"
            )
        )
    }

    private class AtLeastOfCompileData(
        private val conditions: Set<ConfiguredCondition>
    ) : CompileData {
        fun isMet(player: Player, amount: Int) =
            conditions.count { it.isMet(player) } >= amount
    }
}
