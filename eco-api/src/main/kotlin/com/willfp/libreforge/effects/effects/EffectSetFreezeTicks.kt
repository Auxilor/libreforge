package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player

class EffectSetFreezeTicks : Effect(
    "set_freeze_ticks",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim as? Player ?: return

        victim.freezeTicks = config.getIntFromExpression("ticks", data.player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("ticks")) violations.add(
            ConfigViolation(
                "ticks",
                "You must specify the ticks!"
            )
        )

        return violations
    }
}
