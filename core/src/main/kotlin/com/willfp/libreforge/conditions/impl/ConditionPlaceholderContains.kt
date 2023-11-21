package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionPlaceholderContains : Condition<NoCompileData>("placeholder_contains") {
    override val arguments = arguments {
        require("placeholder", "You must specify the placeholder!")
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
