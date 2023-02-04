package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectTransmission : Effect(
    "transmission",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("distance", "You must specify the distance to transmit!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val distance = config.getDoubleFromExpression("distance", data)

        val movement = player.eyeLocation.direction.clone()
            .normalize()
            .multiply(distance)

        val location = player.eyeLocation.clone()
            .add(movement)

        val ray = player.rayTraceBlocks(distance)

        if (ray != null) {
            player.sendMessage(plugin.langYml.getMessage("cannot-transmit"))
            return
        }

        location.pitch = player.location.pitch
        location.yaw = player.location.yaw

        player.teleport(location)
    }
}
