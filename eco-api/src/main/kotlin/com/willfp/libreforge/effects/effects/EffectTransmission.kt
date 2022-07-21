package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectTransmission : Effect(
    "transmission",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val distance = config.getDoubleFromExpression("distance", player)

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

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("distance")) violations.add(
            ConfigViolation(
                "distance",
                "You must specify the distance to transmit!"
            )
        )

        return violations
    }
}
