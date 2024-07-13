package com.willfp.libreforge.effects.impl.aoe.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.angle
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.aoe.AOEShape
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getNearbyBlocksInSphere
import com.willfp.libreforge.toFloat3
import com.willfp.libreforge.toLocation
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.xz
import dev.romainguy.kotlin.math.Float3
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import kotlin.math.abs

object AOEShapeCone: AOEShape<NoCompileData>("cone") {
    override val arguments = arguments {
        require("radius", "You must specify the cone radius!")
        require("angle", "You must specify the angle of the cone!")
    }

    override fun getEntities(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): Collection<LivingEntity> {
        val radius = config.getDoubleFromExpression("radius", data)
        val maxAngle = config.getDoubleFromExpression("angle", data)

        val position = location.xz

        val direction2 = direction.xz

        return location.toLocation(world).getNearbyEntities(radius, radius, radius)
            .filterIsInstance<LivingEntity>()
            .filter {
                val entityPosition = it.location.toFloat3().xz

                val toEntityVector = entityPosition - position

                val angle = Math.toDegrees(direction2.angle(toEntityVector).toDouble())

                abs(angle) <= maxAngle / 2
            }
    }

    override fun getBlocks(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): Collection<Block> {
        val radius = config.getDoubleFromExpression("radius", data)
        val maxAngle = config.getDoubleFromExpression("angle", data)

        val position = location.xz

        val direction2 = direction.xz

        return location.toLocation(world).getNearbyBlocksInSphere(radius)
            .filter {
                val blockPosition = it.location.toFloat3().xz

                val toBlockVector = blockPosition - position

                val angle = Math.toDegrees(direction2.angle(toBlockVector).toDouble())

                abs(angle) <= maxAngle / 2
            }
    }
}
