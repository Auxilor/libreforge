package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.globalPoints
import org.bukkit.entity.Player

object ConditionBelowGlobalPoints : Condition<NoCompileData>("below_global_points") {
    override val description = "Passes when the global points of the given type are below the specified amount."
    override val categories = setOf("economy")

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The global points type identifier.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the maximum amount of points!",
            description = "The global points threshold; the value must be below this amount.",
            type = ArgType.EXPRESSION
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        return globalPoints[config.getString("type")] <
                config.getDoubleFromExpression("amount", dispatcher.get<Player>())
    }
}
