package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectKnockAway : Effect(
    "knock_away",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val victim = data.victim ?: return

        val vector = victim.location.toVector().clone()
            .subtract(player.location.toVector())
            .normalize()
            .multiply(config.getDoubleFromExpression("velocity", data))

        victim.velocity = vector
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
