package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedRegenEvent
import com.willfp.libreforge.triggers.wrappers.WrappedXpEvent
import kotlin.math.ceil

class EffectXpMultiplier : Effect(
    "xp_multiplier",
    supportsFilters = false,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.EVENT
    )
) {
    override fun handle(data: TriggerData, config: JSONConfig) {
        val event = data.event as? WrappedXpEvent ?: return

        event.amount = ceil(event.amount * config.getDouble("multiplier")).toInt()
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                ConfigViolation(
                    "multiplier",
                    "You must specify the xp multiplier!"
                )
            )

        return violations
    }
}