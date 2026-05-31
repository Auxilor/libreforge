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

object ConditionPlaceholderContains : Condition<NoCompileData>("placeholder_contains") {
    override val description = "Passes when the resolved placeholder value contains the specified string."
    override val categories = setOf("meta")

    override val arguments = arguments {
        require(
            "placeholder",
            "You must specify the placeholder!",
            description = "The placeholder string to resolve and check.",
            type = ArgType.STRING
        )
        optional(
            "value",
            description = "The substring to search for within the resolved placeholder.",
            type = ArgType.STRING,
            default = ""
        )
        optional(
            "ignore_case",
            description = "Whether the substring match should be case-insensitive.",
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
            .contains(
                config.getFormattedString("value", placeholderContext(player = player)),
                ignoreCase = config.getBool("ignore_case")
            )
    }
}
