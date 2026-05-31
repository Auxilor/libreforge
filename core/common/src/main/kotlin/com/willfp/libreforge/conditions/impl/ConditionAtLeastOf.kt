package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionAtLeastOf : Condition<ConditionList>("at_least_of") {
    override val description = "Passes when at least the specified number of the listed conditions are met."
    override val categories = setOf("meta")

    override val arguments = arguments {
        require(
            "conditions",
            "You must specify the conditions that can be met!",
            description = "List of conditions to evaluate.",
            type = ArgType.ANY
        )
        require(
            "amount",
            "You must specify the minimum amount of conditions to meet!",
            description = "The minimum number of conditions that must pass.",
            type = ArgType.INT
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: ConditionList
    ): Boolean {
        return compileData.count { it.isMet(dispatcher, holder) } >=
                config.getIntFromExpression("amount", dispatcher.get<Player>())
    }

    override fun makeCompileData(config: Config, context: ViolationContext): ConditionList {
        return Conditions.compile(
            config.getSubsections("conditions"),
            context.with("at_least_of conditions")
        )
    }
}
