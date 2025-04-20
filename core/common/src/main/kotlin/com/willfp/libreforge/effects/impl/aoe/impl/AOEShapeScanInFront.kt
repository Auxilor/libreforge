package com.willfp.libreforge.effects.impl.aoe.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.aoe.AOEShape
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getNearbyBlocksInSphere
import com.willfp.libreforge.normalize
import com.willfp.libreforge.plusAssign
import com.willfp.libreforge.toLocation
import com.willfp.libreforge.triggers.TriggerData
import dev.romainguy.kotlin.math.Float3
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity

object AOEShapeScanInFront : AOEShape<NoCompileData>("scan_in_front") {
    override val arguments = arguments {
        require("radius", "You must specify the scan radius!")
        require("max_distance", "You must specify the maximum distance!")
    }

    override fun getEntities(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): Collection<LivingEntity> {
        val maxDistance = config.getIntFromExpression("max_distance", data)
        val radius = config.getDoubleFromExpression("radius", data)

        val offset = direction.normalize()

        for (i in 1..maxDistance) {
            val entities = (location + offset * i.toFloat()).toLocation(world)
                .getNearbyEntities(radius, radius, radius)
                .filterIsInstance<LivingEntity>()
                .filterNot { it.uniqueId == data.player?.uniqueId }

            if (entities.isNotEmpty()) {
                return entities
            }

            location += offset
        }

        return emptyList()
    }

    override fun getBlocks(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): Collection<Block> {
        val maxDistance = config.getIntFromExpression("max_distance", data)
        val radius = config.getDoubleFromExpression("radius", data)

        val offset = direction.normalize()

        for (i in 0..maxDistance) {
            val blocks = (location + offset * i.toFloat()).toLocation(world)
                .getNearbyBlocksInSphere(radius)
                .filterNot { it.isEmpty }

            if (blocks.isNotEmpty()) {
                return blocks
            }

            location += offset
        }

        return emptyList()
    }
}
