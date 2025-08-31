package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.lumine.mythic.bukkit.events.MythicDamageEvent
import org.bukkit.event.entity.EntityDamageEvent

object EffectDamageMultiplier : Effect<NoCompileData>("damage_multiplier") {
    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require("multiplier", "You must specify the damage multiplier!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val multiplier = config.getDoubleFromExpression("multiplier", data)

        when (val event = data.event) {
            is EntityDamageEvent -> {
                event.damage *= multiplier
                return true
            }
            is MythicDamageEvent -> {
                event.damage *= multiplier
                return true
            }
        }

        return false
    }
}