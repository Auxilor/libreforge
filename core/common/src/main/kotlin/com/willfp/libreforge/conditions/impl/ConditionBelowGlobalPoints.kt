package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.globalPoints
import org.bukkit.entity.Player

object ConditionBelowGlobalPoints : Condition<NoCompileData>("below_global_points") {
    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("amount", "You must specify the maximum amount of points!")
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
