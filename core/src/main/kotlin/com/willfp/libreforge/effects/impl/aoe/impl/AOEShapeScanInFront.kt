package com.willfp.libreforge.effects.impl.aoe.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.impl.aoe.AOEShape
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import dev.romainguy.kotlin.math.Float3
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import kotlin.math.roundToInt

object AOEShapeScanInFront: AOEShape<NoCompileData>("scan_in_front") {
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

        for (i in 0..maxDistance) {
            val entities = location.toLocation(world).getNearbyEntities(radius, radius, radius)
                .filterIsInstance<LivingEntity>()
                .filterNot { it.uniqueId == data.player?.uniqueId }

            if (entities.isNotEmpty()) {
                return entities
            }

            location += offset
        }

        return emptyList()
    }
    // todo: change this, this is copied from shape circle
    override fun getBlocks(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): Collection<Block> {
        val radius = config.getDoubleFromExpression("radius", data)
        val blocks = arrayListOf<Block>()
        val radiusInt = radius.roundToInt()

        for (x in (-radiusInt..radiusInt)) {
            for (y in (-radiusInt..radiusInt)) {
                for (z in (-radiusInt..radiusInt)) {
                    val block = world.getBlockAt(
                        location.toLocation(world).clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    )
                    plugin.logger.info("block being added: ${block.type.name}")
                    blocks.add(block)
                }
            }
        }
        return blocks.filter { !it.isEmpty }
    }
}
