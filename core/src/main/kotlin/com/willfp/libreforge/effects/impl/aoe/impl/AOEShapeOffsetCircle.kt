package com.willfp.libreforge.effects.impl.aoe.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.aoe.AOEShape
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getNearbyBlocksInSphere
import com.willfp.libreforge.normalize
import com.willfp.libreforge.toLocation
import com.willfp.libreforge.triggers.TriggerData
import dev.romainguy.kotlin.math.Float3
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity

object AOEShapeOffsetCircle : AOEShape<NoCompileData>("offset_circle") {
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

    override fun getBlocks(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): Collection<Block> {
        val radius = config.getDoubleFromExpression("radius", data)
        val offset = config.getDoubleFromExpression("offset", data)

        return (location + direction.normalize() * offset.toFloat())
            .toLocation(world)
            .getNearbyBlocksInSphere(radius)
    }
}
