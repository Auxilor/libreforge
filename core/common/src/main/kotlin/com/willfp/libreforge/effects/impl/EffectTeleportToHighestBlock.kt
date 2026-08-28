package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Location

object EffectTeleportToHighestBlock : Effect<NoCompileData>("teleport_to_highest_block") {
    override val description = "Teleports the player straight up to the highest block at their coordinates."
    override val categories = setOf("movement")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = player.location
        val world = location.world ?: return false

        val y = world.getHighestBlockYAt(location)

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
}
