package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Location

object EffectTeleportToLowestBlock : Effect<NoCompileData>("teleport_to_lowest_block") {
    override val description = "Teleports the player down to the lowest safe standing position at their coordinates."
    override val categories = setOf("movement")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = player.location
        val world = location.world ?: return false

        for (y in world.minHeight until location.blockY) {
            val floor = world.getBlockAt(location.blockX, y, location.blockZ)
            val feet = world.getBlockAt(location.blockX, y + 1, location.blockZ)
            val head = world.getBlockAt(location.blockX, y + 2, location.blockZ)

            if (!floor.type.isSolid || !feet.isPassable || !head.isPassable) {
                continue
            }

            player.teleport(
                Location(
                    world,
                    location.blockX + 0.5,
                    (y + 1).toDouble(),
                    location.blockZ + 0.5,
                    location.yaw,
                    location.pitch
                )
            )

            return true
        }

        return false
    }
}
