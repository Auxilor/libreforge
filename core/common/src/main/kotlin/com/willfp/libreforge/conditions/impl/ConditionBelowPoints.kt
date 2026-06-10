package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.points
import org.bukkit.entity.Player

object ConditionBelowPoints : Condition<NoCompileData>("below_points") {
    override val description = "Passes when the player's points of the given type are below the specified amount."
    override val categories = setOf("economy")

    override val arguments = arguments {
        require(
            "type",
            "You must specify the type of points!",
            description = "The points type identifier.",
            type = ArgType.STRING
        )
        require(
            "amount",
            "You must specify the maximum amount of points!",
            description = "The points threshold; the player must be below this amount.",
            type = ArgType.EXPRESSION
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.points[config.getString("type")] < config.getDoubleFromExpression("amount", player)
    }
}
