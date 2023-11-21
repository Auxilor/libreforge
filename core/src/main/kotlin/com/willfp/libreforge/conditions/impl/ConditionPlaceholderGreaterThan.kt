package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.get
import org.bukkit.entity.Player


object ConditionPlaceholderGreaterThan : Condition<NoCompileData>("placeholder_greater_than") {
    override val arguments = arguments {
        require("placeholder", "You must specify the placeholder!")
        require("value", "You must specify the value!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>()

        return (config.getFormattedString("placeholder", placeholderContext(player = player))
            .toDoubleOrNull() ?: 0.0) > config.getDoubleFromExpression("value", player)
    }
}
