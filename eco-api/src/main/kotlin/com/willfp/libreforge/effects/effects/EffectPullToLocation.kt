package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectPullToLocation : Effect(
    "pull_to_location",
    supportsFilters = true,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val location = data.location ?: return

        if (player.world != location.world) {
            return
        }

        val vector = location.toVector().subtract(player.location.toVector()).normalize()
            .multiply(config.getDoubleFromExpression("velocity"))

        player.velocity = vector
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("velocity")) violations.add(
            ConfigViolation(
                "velocity",
                "You must specify the movement velocity!"
            )
        )

        return violations
    }
}