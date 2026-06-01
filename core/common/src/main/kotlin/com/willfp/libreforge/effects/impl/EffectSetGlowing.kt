package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.plugin
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetGlowing : Effect<NoCompileData>("set_glowing") {
    override val description = "Makes the target entity glow, optionally reverting after a duration."
    override val categories = setOf("visual")

    override val arguments = arguments {
        optional(
            "glowing",
            description = "Whether to enable or disable the glowing effect.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "duration",
            description = "How long to apply the glow, in ticks. Reverts when expired. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }
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
