package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player


object ConditionPlaceholderLessThan : Condition<NoCompileData>("placeholder_less_than") {
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

        return (config.getDoubleFromExpression("placeholder", placeholderContext(player = player))) <
                config.getDoubleFromExpression("value", placeholderContext(player = player))
    }
}
