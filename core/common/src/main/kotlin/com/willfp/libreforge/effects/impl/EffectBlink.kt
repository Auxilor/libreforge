package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectBlink : Effect<NoCompileData>("blink") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("distance", "You must specify the blink distance!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val distance = config.getIntFromExpression("distance", data)

        val direction = player.location.direction.normalize()
        var destination = player.location.clone()

        for (i in 1..distance) {
            val next = player.location.clone().add(direction.clone().multiply(i))
            if (!next.block.isPassable) break
            destination = next
        }

        destination.yaw = player.location.yaw
        destination.pitch = player.location.pitch

        if (Prerequisite.HAS_PAPER.isMet) {
            player.teleportAsync(destination)
        } else {
            player.teleport(destination)
        }

        return true
    }
}
