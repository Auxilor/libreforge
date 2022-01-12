package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectStrikeLightning : Effect(
    "strike_lightning",
    supportsFilters = false,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val world = location.world ?: return

        for (i in 1..config.getIntFromExpression("amount", data.player)) {
            plugin.scheduler.runLater({
                world.strikeLightning(location)
            }, 1)
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("amount")) violations.add(
                ConfigViolation(
                    "amount",
                    "You must specify the amount of lightning!"
                )
            )

        return violations
    }
}