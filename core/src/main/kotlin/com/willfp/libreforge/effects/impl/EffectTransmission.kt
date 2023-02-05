package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

object EffectTransmission : Effect<NoCompileData>("transmission") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("distance", "You must specify the distance to transmit!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val distance = config.getDoubleFromExpression("distance", data)

        val movement = player.eyeLocation.direction.clone()
            .normalize()
            .multiply(distance)

        val location = player.eyeLocation.clone()
            .add(movement)

        val ray = player.rayTraceBlocks(distance)

        if (ray != null) {
            player.sendMessage(plugin.langYml.getMessage("cannot-transmit"))
            return false
        }

        location.pitch = player.location.pitch
        location.yaw = player.location.yaw

        player.teleport(location)

        return true
    }
}
