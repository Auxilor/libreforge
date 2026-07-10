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

object ConditionAbovePoints : Condition<NoCompileData>("above_points") {
    override val description = "Passes when the player's points of the given type are at or above the minimum."
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
            "You must specify the minimum amount of points!",
            description = "The minimum number of points required.",
            type = ArgType.EXPRESSION,
            example = "%level% * 10"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.points[config.getString("type")] >= config.getDoubleFromExpression("amount", player)
    }
}
