package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.triggers.TriggerCustom

class EffectTriggerCustom : Effect(
    "trigger_custom",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    ),
    noDelay = true
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val trigger = TriggerCustom.getWithID(config.getString("trigger"))
        val value = config.getDoubleFromExpression("value", player)

        trigger.invoke(player, data, value)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("trigger")) violations.add(
            ConfigViolation(
                "trigger",
                "You must specify the custom trigger ID!"
            )
        )

        return violations
    }
}
