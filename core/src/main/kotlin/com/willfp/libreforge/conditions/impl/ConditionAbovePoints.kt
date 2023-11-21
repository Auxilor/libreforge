package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.points
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.get
import org.bukkit.entity.Player

object ConditionAbovePoints : Condition<NoCompileData>("above_points") {
    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("amount", "You must specify the minimum amount of points!")
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
