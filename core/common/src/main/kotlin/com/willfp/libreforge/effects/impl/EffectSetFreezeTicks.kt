package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetFreezeTicks : Effect<NoCompileData>("set_freeze_ticks") {
    override val description = "Sets the victim's freeze ticks, controlling how frozen they appear and whether they take freeze damage."
    override val categories = setOf("player")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "ticks",
            "You must specify the freeze ticks!",
            description = "The number of freeze ticks to apply to the victim. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        victim.freezeTicks = config.getIntFromExpression("ticks", data)

        return true
    }
}
