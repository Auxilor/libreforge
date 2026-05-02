package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.plugin
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetGlowing : Effect<NoCompileData>("set_glowing") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val glowing = config.getBoolOrNull("glowing") ?: true
        val duration = config.getOrNull("duration") { getIntFromExpression(it, data) }

        victim.isGlowing = glowing

        if (duration != null && duration > 0) {
            plugin.scheduler.runLater(duration.toLong()) {
                victim.isGlowing = !glowing
            }
        }

        return true
    }
}
