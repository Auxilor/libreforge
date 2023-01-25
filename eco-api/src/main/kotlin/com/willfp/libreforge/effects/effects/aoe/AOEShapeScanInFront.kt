package com.willfp.libreforge.effects.effects.aoe

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.effects.particles.toLocation
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.joml.Vector3f

object AOEShapeScanInFront: AOEShape("scan_in_front") {
    override val arguments = arguments {
        require("radius", "You must specify the scan radius!")
        require("max_distance", "You must specify the maximum distance!")
    }

    override fun getEntities(
        location: Vector3f,
        direction: Vector3f,
        world: World,
        config: Config,
        data: TriggerData
    ): Collection<LivingEntity> {
        val maxDistance = config.getIntFromExpression("max_distance", data)
        val radius = config.getDoubleFromExpression("radius", data)

        val offset = direction.normalize()

        for (i in 0..maxDistance) {
            val entities = location.toLocation(world).getNearbyEntities(radius, radius, radius)
                .filterIsInstance<LivingEntity>()
                .filterNot { it.uniqueId == data.player?.uniqueId }

            if (entities.isNotEmpty()) {
                return entities
            }

            location.add(offset)
        }

        return emptyList()
    }
}
