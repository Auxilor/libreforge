package com.willfp.libreforge.effects.effects.aoe

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.effects.particles.DirectionVector
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

object AOEShapeCircle: AOEShape("circle") {
    override fun getEntities(
        location: Vector3f,
        direction: Vector3f,
        world: World,
        config: Config,
        data: TriggerData
    ): Collection<LivingEntity> {
        val radius = config.getDoubleFromExpression("radius", data)

        return location.toLocation(world).getNearbyEntities(radius, radius, radius)
            .filterIsInstance<LivingEntity>()
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the circle radius!"
            )
        )

        return violations
    }
}
