package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent

class EffectDamageMultiplier : Effect(
    "damage_multiplier",
    triggers = Triggers.withParameters(
        TriggerParameter.EVENT
    ),
    noDelay = true
) {
    override fun handle(data: TriggerData, config: Config) {
        val event = data.event as? WrappedDamageEvent ?: return

        event.damage *= config.getDoubleFromExpression("multiplier", data)

        if (event.damage < 0.01) {
            event.isCancelled = true
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "multiplier",
                "You must specify the damage multiplier!"
            )
        )

        return violations
    }
}