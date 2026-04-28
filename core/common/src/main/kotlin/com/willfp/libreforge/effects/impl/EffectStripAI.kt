package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Mob

object EffectStripAI : Effect<NoCompileData>("strip_ai") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("duration", "You must specify the duration to disable AI for!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim as? Mob ?: return false

        val duration = config.getIntFromExpression("duration", data)

        val aiBefore = victim.hasAI()

        if (!aiBefore) {
            return false
        }

        victim.setAI(false)

        plugin.scheduler.runLater(duration.toLong()) {
            victim.setAI(true)
        }

        return true
    }
}
