package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedHungerEvent
import kotlin.math.ceil

class EffectHungerMultiplier : Effect(
    "hunger_multiplier",
    supportsFilters = true,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.EVENT
    )
) {
    override fun handle(data: TriggerData, config: JSONConfig) {
        val event = data.event as? WrappedHungerEvent ?: return

        val multiplier = config.getDouble("multiplier")

        if (multiplier < 1) {
            if (NumberUtils.randFloat(0.0, 1.0) > multiplier) {
                event.isCancelled = true
            }
        } else {
            event.amount = ceil(event.amount * multiplier).toInt()
        }
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                ConfigViolation(
                    "multiplier",
                    "You must specify the hunger multiplier!"
                )
            )

        return violations
    }
}
