package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions

object ConditionAnyOf : Condition<ConditionList>("any_of") {
    override val arguments = arguments {
        require("conditions", "You must specify the conditions that can be met!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: ConditionList
    ): Boolean {
        return compileData.any { it.isMet(dispatcher, holder) }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): ConditionList {
        return Conditions.compile(config.getSubsections("conditions"), context.with("any_of conditions"))
    }
}
