package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionIsExpressionTrue : Condition<NoCompileData>("is_expression_true") {
    override val description = "Passes when the given mathematical expression evaluates to 1."

    override val categories = setOf("meta")

    override val additionalInfo = listOf(
        "The expression must evaluate to exactly 1.0 to pass — any other value (including 0) fails."
    )

    override val arguments = arguments {
        require(
            "expression",
            "You must specify the expression!",
            description = "A mathematical expression or placeholder that must evaluate to 1 for the condition to pass.",
            type = ArgType.EXPRESSION,
            example = "%player_level% >= 10"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>()

        return config.getDoubleFromExpression("expression", player) == 1.0
    }
}
