package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.CompileData
import org.bukkit.entity.Player

class ConditionAnyOf : Condition("any_of") {
    override val arguments = arguments {
        require("conditions", "You must specify the conditions that can be met!")
    }

    override fun isConditionMet(player: Player, config: Config, data: CompileData?): Boolean {
        val anyOfData = data as? AnyOfCompileData ?: return true

        return anyOfData.isMet(player)
    }

    override fun makeCompileData(config: Config, context: String): CompileData {
        return AnyOfCompileData(
            Conditions.compile(
                config.getSubsections("conditions"),
                "$context -> any_of Conditions)"
            )
        )
    }

    private class AnyOfCompileData(
        private val conditions: Set<ConfiguredCondition>
    ) : CompileData {
        fun isMet(player: Player) = conditions.any { it.isMet(player) }
    }
}
