package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedRegenEvent

class EffectRegenMultiplier : Effect(
    "regen_multiplier",
    supportsFilters = true,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.EVENT
    )
) {
    override fun handle(data: TriggerData, config: JSONConfig) {
        val event = data.event as? WrappedRegenEvent ?: return

        event.amount *= config.getDouble("multiplier")
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                ConfigViolation(
                    "multiplier",
                    "You must specify the regen multiplier!"
                )
            )

        return violations
    }
}