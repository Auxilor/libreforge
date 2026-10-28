package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.Regions
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
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
        val location = dispatcher.get<LivingEntity>()?.location ?: return false
        val world = location.world ?: return false
        val radius = config.getInt("radius")
        val targetMaterial = Material.matchMaterial(config.getString("block").uppercase()) ?: return false
        val minY = (location.blockY - radius).coerceAtLeast(world.minHeight)
        val maxY = (location.blockY + radius).coerceAtMost(world.maxHeight - 1)
        val areaReachable = Regions.canReach(location, radius.toDouble())

        for (y in minY..maxY) {
            for (x in location.blockX - radius..location.blockX + radius) {
                for (z in location.blockZ - radius..location.blockZ + radius) {
                    val reachable = areaReachable || Regions.canReach(blockLocation(world, x, y, z))

                    if (reachable && world.getType(x, y, z) == targetMaterial) {
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun blockLocation(world: World, x: Int, y: Int, z: Int) =
        Location(world, x.toDouble(), y.toDouble(), z.toDouble())
}
