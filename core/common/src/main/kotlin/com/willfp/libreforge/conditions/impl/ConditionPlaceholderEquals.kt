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


object ConditionPlaceholderEquals : Condition<NoCompileData>("placeholder_equals") {
    override val description = "Passes when the resolved placeholder value equals the specified string."
    override val categories = setOf("meta")

    override val arguments = arguments {
        require(
            "placeholder",
            "You must specify the placeholder!",
            description = "The placeholder string to resolve and compare.",
            type = ArgType.STRING
        )
        require(
            "value",
            "You must specify the value!",
            description = "The exact string the resolved placeholder must equal.",
            type = ArgType.STRING
        )
        optional(
            "ignore_case",
            description = "Whether the equality check should be case-insensitive.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>()

        return config.getFormattedString("placeholder", placeholderContext(player = player))
            .equals(config.getFormattedString("value", placeholderContext(player = player)),
                ignoreCase = config.getBool("ignore_case"))
    }
}
