package com.willfp.libreforge.integrations.edprisoncore.impl

import com.edwardbelt.edprison.modules.robots.RobotUtils
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionHasEdPrisonRobot : Condition<NoCompileData>("has_edprison_robot") {
    override val arguments = arguments {
        require("robot", "You must specify the robot type!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>()?.uniqueId ?: return false
        val robot = config.getString("robot")

        val playerRobots = RobotUtils.getPlayerRobots(player)

        return playerRobots.contains(robot)
    }
}