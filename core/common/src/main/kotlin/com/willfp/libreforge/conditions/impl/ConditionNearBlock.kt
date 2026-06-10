package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.Material
import org.bukkit.entity.LivingEntity

object ConditionNearBlock : Condition<NoCompileData>("near_block") {
    override val description = "Passes when a block of the specified type is found within a cubic radius of the entity."
    override val categories = setOf("world")

    override val arguments = arguments {
        require(
            "block",
            "You must specify the block type!",
            description = "The block type to search for.",
            type = ArgType.BLOCK
        )
        require(
            "radius",
            "You must specify the radius!",
            description = "The radius of the cube around the entity to search within.",
            type = ArgType.INT
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val entity = dispatcher.get<LivingEntity>() ?: return false
        val location = entity.location
        val radius = config.getInt("radius")
        val targetMaterial = Material.matchMaterial(config.getString("block").uppercase()) ?: return false

        for (x in -radius..radius) {
            for (y in -radius..radius) {
                for (z in -radius..radius) {
                    val block = location.world.getBlockAt(
                        location.blockX + x,
                        location.blockY + y,
                        location.blockZ + z
                    )
                    if (block.type == targetMaterial) return true
                }
            }
        }

        return false
    }
}
