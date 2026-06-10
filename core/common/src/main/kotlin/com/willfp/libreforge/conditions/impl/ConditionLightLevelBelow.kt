package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get

object ConditionLightLevelBelow : Condition<NoCompileData>("light_level_below") {
    override val description = "Passes when the light level at the dispatcher's location is at or below the specified value."
    override val categories = setOf("world")

    override val arguments = arguments {
        require(
            "level",
            "You must specify maximum light level!",
            description = "The maximum light level (0–15) allowed at the location.",
            type = ArgType.INT
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val location = dispatcher.location ?: return false

        val level = config.getIntFromExpression("level", dispatcher.get())

        return location.block.lightLevel <= level
    }
}
