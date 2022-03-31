package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.CompileData
import org.bukkit.entity.Player

class ConditionAnyOf : Condition("any_of") {
    override fun isConditionMet(player: Player, config: Config, data: CompileData?): Boolean {
        val anyOfData = data as? AnyOfCompileData ?: return true

        return anyOfData.isMet(player)
    }

    override fun makeCompileData(config: Config, context: String): CompileData {
        return AnyOfCompileData(config.getSubsections("conditions").mapNotNull {
            Conditions.compile(it, "$context (Any of conditions)")
        })
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("conditions")) violations.add(
            ConfigViolation(
                "x",
                "You must specify the conditions that can be met!"
            )
        )

        return violations
    }

    private class AnyOfCompileData(
        override val data: Iterable<ConfiguredCondition>
    ) : CompileData {
        fun isMet(player: Player): Boolean {
            val list = data.toList()

            if (list.isEmpty()) {
                return true
            }

            return list.any { it.condition.isConditionMet(player, it.config) }
        }
    }
}