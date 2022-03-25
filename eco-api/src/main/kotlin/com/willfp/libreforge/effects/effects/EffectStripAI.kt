package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Mob

class EffectStripAI : Effect(
    "strip_ai",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim as? Mob ?: return

        val duration = config.getIntFromExpression("duration", data.player)

        val aiBefore = victim.hasAI()

        if (!aiBefore) {
            return
        }

        victim.setAI(false)

        plugin.scheduler.runLater(duration.toLong()) {
            victim.setAI(true)
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("duration")) violations.add(
            ConfigViolation(
                "duration",
                "You must the duration to disable AI for!"
            )
        )

        return violations
    }
}