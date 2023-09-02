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

object AOEShapeBeam : AOEShape<NoCompileData>("beam") {
    override val arguments = arguments {
        require("radius", "You must specify the beam radius!")
        require("distance", "You must specify the distance!")
        require("pierce_blocks", "You must specify whether the beam pierces blocks!")
        require("pierce_entities", "You must specify whether the beam pierces entities!")
    }

    override fun getEntities(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): Collection<LivingEntity> {
        val distance = config.getIntFromExpression("distance", data)
        val radius = config.getDoubleFromExpression("radius", data)

        val piercesBlocks = config.getBool("pierce_blocks")
        val piercesEntities = config.getBool("pierce_entities")

        val offset = direction.normalize()

        val entities = mutableSetOf<LivingEntity>()

        for (i in 1..distance) {
            val center = (location + offset * i.toFloat()).toLocation(world)

            val atBeamPoint = center.getNearbyEntities(radius, radius, radius)
                .filterIsInstance<LivingEntity>()
                .filterNot { it.uniqueId == data.player?.uniqueId }

            entities += atBeamPoint

            if (!piercesEntities && entities.isNotEmpty()) {
                return entities
            }

            if (!piercesBlocks && !center.block.isEmpty) {
                return entities
            }

            location += offset
        }

        return entities
    }

    override fun getBlocks(
        location: Float3,
        direction: Float3,
        world: World,
        config: Config,
        data: TriggerData,
        compileData: NoCompileData
    ): Collection<Block> {
        val distance = config.getIntFromExpression("distance", data)
        val radius = config.getDoubleFromExpression("radius", data)

        val piercesBlocks = config.getBool("pierce_blocks")
        val piercesEntities = config.getBool("pierce_entities")

        val offset = direction.normalize()

        val blocks = mutableSetOf<Block>()

        for (i in 0..distance) {
            val center = (location + offset * i.toFloat()).toLocation(world)

            val atBeamPoint = center.getNearbyBlocksInSphere(radius)

            blocks += atBeamPoint

            val entitiesAtPoint = center.getNearbyEntities(radius, radius, radius)
                .filterIsInstance<LivingEntity>()
                .filterNot { it.uniqueId == data.player?.uniqueId }

            if (!piercesEntities && entitiesAtPoint.isNotEmpty()) {
                return blocks
            }

            if (!piercesBlocks && !center.block.isEmpty) {
                return blocks
            }

            location += offset
        }

        return blocks
    }
}
