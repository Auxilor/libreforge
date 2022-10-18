package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent

class EffectAddDamage : Effect(
    "add_damage",
    triggers = Triggers.withParameters(
        TriggerParameter.EVENT
    ),
    noDelay = true,
    runOrder = RunOrder.LATE
) {
    override fun handle(data: TriggerData, config: Config) {
        val event = data.event as? WrappedDamageEvent ?: return

        event.damage += config.getDoubleFromExpression("damage", data)

        if (event.damage < 0.01) {
            event.isCancelled = true
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("damage")) violations.add(
            ConfigViolation(
                "damage",
                "You must specify the damage to add!"
            )
        )

        return violations
    }
}
