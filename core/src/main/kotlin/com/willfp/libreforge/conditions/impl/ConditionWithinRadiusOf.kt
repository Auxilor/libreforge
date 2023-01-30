package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.pow

object ConditionWithinRadiusOf : Condition<NoCompileData>("within_radius_of") {
    override val arguments = arguments {
        require("x", "You must specify the x coordinate!")
        require("y", "You must specify the y coordinate!")
        require("z", "You must specify the z coordinate!")
        require("radius", "You must specify the radius!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        val vector = Vector(
            config.getDoubleFromExpression("x", player),
            config.getDoubleFromExpression("y", player),
            config.getDoubleFromExpression("z", player)
        )

        val dist = config.getDoubleFromExpression("radius", player).pow(2)

        return player.location.toVector().distanceSquared(vector) <= dist
    }
}
