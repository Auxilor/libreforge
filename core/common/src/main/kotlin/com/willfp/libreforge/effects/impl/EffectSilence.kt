package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSilence : Effect<NoCompileData>("silence") {
    override val description = "Silences the victim entity for a duration, preventing it from making sounds."
    override val categories = setOf("entity")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "ticks",
            "You must specify the silence duration in ticks!",
            description = "How long to silence the entity, in ticks. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val ticks = config.getIntFromExpression("ticks", data)

        victim.isSilent = true
        plugin.scheduler.runLater(ticks.toLong()) {
            victim.isSilent = false
        }

        return true
    }
}
