package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get

object ConditionBelowY : Condition<NoCompileData>("below_y") {
    override val description = "Passes when the entity's Y coordinate is below the specified value."
    override val categories = setOf("world")

    override val arguments = arguments {
        require(
            "y",
            "You must specify the y level!",
            description = "The Y coordinate threshold; the entity must be below this value.",
            type = ArgType.EXPRESSION
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val location = dispatcher.location ?: return false
        return location.y < config.getDoubleFromExpression("y", dispatcher.get())
    }
}
