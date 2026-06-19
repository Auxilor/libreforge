package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player


object ConditionPlaceholderLessThan : Condition<NoCompileData>("placeholder_less_than") {
    override val description = "Passes when the resolved placeholder value is numerically less than the specified value."
    override val categories = setOf("meta")

    override val arguments = arguments {
        require(
            "placeholder",
            "You must specify the placeholder!",
            description = "The placeholder string to resolve and evaluate as a number.",
            type = ArgType.STRING
        )
        require(
            "value",
            "You must specify the value!",
            description = "The numeric threshold the placeholder must be strictly below.",
            type = ArgType.EXPRESSION
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>()

        return (config.getDoubleFromExpression("placeholder", placeholderContext(player = player))) <
                config.getDoubleFromExpression("value", placeholderContext(player = player))
    }
}
