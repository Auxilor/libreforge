package com.willfp.libreforge.effects.effects.aoe

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.effects.particles.toLocation
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.joml.Vector3f

object AOEShapeOffsetCircle: AOEShape("offset_circle") {
    override val arguments = arguments {
        require("radius", "You must specify the circle radius!")
        require("offset", "You must specify the circle offset!")
    }

    override fun getEntities(
        location: Vector3f,
        direction: Vector3f,
        world: World,
        config: Config,
        data: TriggerData
    ): Collection<LivingEntity> {
        val radius = config.getDoubleFromExpression("radius", data)
        val offset = config.getDoubleFromExpression("offset", data)

        return location.add(direction.normalize(offset.toFloat()))
            .toLocation(world)
            .getNearbyEntities(radius, radius, radius).filterIsInstance<LivingEntity>()
    }
}
