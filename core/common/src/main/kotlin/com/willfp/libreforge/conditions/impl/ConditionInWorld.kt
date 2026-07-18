package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition

object ConditionInWorld : Condition<NoCompileData>("in_world") {
    override val description = "Passes when the dispatcher is located in the specified world."

    override val categories = setOf("world")

    override val arguments = arguments {
        require(
            "world",
            "You must specify the world name!",
            description = "The name of the world to check against.",
            type = ArgType.STRING
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val location = dispatcher.location ?: return false

        return location.world.name.equals(config.getString("world"), ignoreCase = true)
    }
}
