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

object AOEShapeOffsetCircle: AOEShape<NoCompileData>("offset_circle") {
    override val arguments = arguments {
        require("radius", "You must specify the circle radius!")
        require("offset", "You must specify the circle offset!")
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
        val offset = config.getDoubleFromExpression("offset", data)

        return (location + direction.normalize() * offset.toFloat())
            .toLocation(world)
            .getNearbyEntities(radius, radius, radius).filterIsInstance<LivingEntity>()
    }
    // todo: change this, copied from shape circle
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
        return blocks.filter { it.isEmpty }
    }
}
