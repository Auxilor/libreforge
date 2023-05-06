package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.entity.EntityDamageEvent

object EffectCritMultiplier : Effect<NoCompileData>("crit_multiplier") {
    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require("multiplier", "You must specify the crit damage multiplier!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityDamageEvent ?: return false
        val player = data.player ?: return false

        if (player.velocity.y >= -0.1) {
            return false
        }

        event.damage *= config.getDoubleFromExpression("multiplier", data)
        return true
    }
}
