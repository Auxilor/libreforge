package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Material

class EffectTeleportToGround : Effect(
    "teleport_to_ground",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val location = player.location
        val world = location.world ?: return

        var current = location.clone()
        if (current.y > world.maxHeight) {
            current.y = world.maxHeight.toDouble()
        }

        while (world.getBlockAt(current).type == Material.AIR) {
            if (current.y <= world.minHeight) {
                return
            }
            current = current.subtract(0.0, 1.0, 0.0)
        }

        player.teleport(current)
    }
}
