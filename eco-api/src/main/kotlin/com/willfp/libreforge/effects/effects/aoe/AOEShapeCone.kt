package com.willfp.libreforge.effects.effects.aoe

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.effects.particles.copy
import com.willfp.libreforge.effects.effects.particles.toLocation
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.joml.Vector2d
import org.joml.Vector2f
import org.joml.Vector3f

object AOEShapeCone: AOEShape("cone") {
    override fun getEntities(
        location: Vector3f,
        direction: Vector3f,
        world: World,
        config: Config,
        data: TriggerData
    ): Collection<LivingEntity> {
        val radius = config.getDoubleFromExpression("radius", data)
        val maxAngle = config.getDoubleFromExpression("angle", data)

        val position = Vector2f(
            location.x,
            location.z
        )

        val direction2 = Vector2f(
            direction.x,
            direction.z
        )

        return location.toLocation(world).getNearbyEntities(radius, radius, radius)
            .filterIsInstance<LivingEntity>()
            .filter {
                val entityPosition = Vector2f(
                    it.location.x.toFloat(),
                    it.location.z.toFloat()
                )

                val toEntityVector = entityPosition.copy().sub(position)

                val angle = Math.toDegrees(direction2.angle(toEntityVector).toDouble())

                angle <= maxAngle
            }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the cone radius!"
            )
        )

        if (!config.has("angle")) violations.add(
            ConfigViolation(
                "angle",
                "You must specify the angle of the cone!"
            )
        )

        return violations
    }
}
