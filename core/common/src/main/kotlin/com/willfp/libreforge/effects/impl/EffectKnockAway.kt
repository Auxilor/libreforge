package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectKnockAway : Effect<NoCompileData>("knock_away") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("velocity", "You must specify the movement velocity!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val victim = data.victim ?: return false

        val vector = victim.location.toVector().clone()
            .subtract(player.location.toVector())
            .normalize()
            .multiply(config.getDoubleFromExpression("velocity", data))

        victim.velocity = vector

        return true
    }
}
