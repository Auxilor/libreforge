package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectStrikeLightning : Effect(
    "strike_lightning",
    triggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val world = location.world ?: return

        val amount = if (config.has("amount")) config.getIntFromExpression("amount", data) else 1

        for (i in 1..amount) {
            plugin.scheduler.runLater({
                world.strikeLightning(location)
            }, 1)
        }
    }
}
