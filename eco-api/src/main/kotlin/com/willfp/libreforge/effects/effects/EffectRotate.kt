package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectRotate : Effect(
    "rotate",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val yaw = player.location.yaw + config.getDoubleFromExpression("angle", player).toFloat()


        player.location.yaw = yaw % 360f
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("angle")) violations.add(
            ConfigViolation(
                "angle",
                "You must specify the angle to rotate around!"
            )
        )

        return violations
    }
}
