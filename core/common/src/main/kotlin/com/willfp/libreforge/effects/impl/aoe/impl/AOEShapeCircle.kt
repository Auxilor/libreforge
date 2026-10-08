package com.willfp.libreforge.effects.impl.aoe.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.Regions
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.impl.aoe.AOEShape
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getNearbyBlocksInSphere
import com.willfp.libreforge.toLocation
import com.willfp.libreforge.triggers.TriggerData
import dev.romainguy.kotlin.math.Float3
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity

object AOEShapeCircle : AOEShape<NoCompileData>("circle") {
    override val arguments = arguments {
        require("radius", "You must specify the circle radius!")
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
        val center = location.toLocation(world)

        if (!Regions.canReach(center, radius)) {
            return emptyList()
        }

        return center.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<LivingEntity>()
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
        val center = location.toLocation(world)

        if (!Regions.canReach(center, radius)) {
            return emptyList()
        }

        return center.getNearbyBlocksInSphere(radius)
    }
}
