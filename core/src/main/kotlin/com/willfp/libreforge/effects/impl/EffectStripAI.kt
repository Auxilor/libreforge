package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Mob

class EffectStripAI : Effect(
    "strip_ai",
    triggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    override val arguments = arguments {
        require("duration", "You must specify the duration to disable AI for!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim as? Mob ?: return

        val duration = config.getIntFromExpression("duration", data)

        val aiBefore = victim.hasAI()

        if (!aiBefore) {
            return
        }

        victim.setAI(false)

        plugin.scheduler.runLater(duration.toLong()) {
            victim.setAI(true)
        }
    }
}
