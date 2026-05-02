package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRandomTeleport : Effect<NoCompileData>("random_teleport") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val radius = config.getIntFromExpression("radius", data)
        val minRadius = config.getOrNull("min_radius") { getIntFromExpression(it, data) } ?: 0

        val loc = player.location.clone()
        val world = loc.world ?: return false

        var offsetX: Int
        var offsetZ: Int
        do {
            offsetX = NumberUtils.randInt(-radius, radius)
            offsetZ = NumberUtils.randInt(-radius, radius)
        } while (offsetX * offsetX + offsetZ * offsetZ < minRadius * minRadius)

        loc.x += offsetX
        loc.z += offsetZ
        loc.y = world.getHighestBlockYAt(loc).toDouble() + 1

        if (loc.y < world.minHeight || loc.y > world.maxHeight) return false

        if (Prerequisite.HAS_PAPER.isMet) {
            player.teleportAsync(loc)
        } else {
            player.teleport(loc)
        }

        return true
    }
}
