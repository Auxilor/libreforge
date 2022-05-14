package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.pow

class ConditionWithinRadiusOf : Condition("within_radius_of") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        val vector = Vector(
            config.getDoubleFromExpression("x", player),
            config.getDoubleFromExpression("y", player),
            config.getDoubleFromExpression("z", player)
        )

        val dist = config.getDoubleFromExpression("radius", player).pow(2)

        return player.location.toVector().distanceSquared(vector) <= dist
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("x")) violations.add(
            ConfigViolation(
                "x",
                "You must specify the x coordinate!"
            )
        )

        if (!config.has("y")) violations.add(
            ConfigViolation(
                "y",
                "You must specify the y coordinate!"
            )
        )

        if (!config.has("z")) violations.add(
            ConfigViolation(
                "z",
                "You must specify the z coordinate!"
            )
        )

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the radius!"
            )
        )

        return violations
    }
}