package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getDoubleFromExpressionDebug
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.lumine.mythic.bukkit.events.MythicDamageEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.entity.EntityDamageEvent

object EffectTotalDamageMultiplier : Effect<NoCompileData>("total_damage_multiplier") {
    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.EVENT
    )

    override val arguments = arguments {
        require("multiplier", "You must specify the damage multiplier!")
    }

    override val runOrder = RunOrder.END

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        if(data.event is EntityDamageEvent){
            data.event.damage *= config.getDoubleFromExpression("multiplier", data)
            return true
        }
        if(data.event is MythicDamageEvent){
            data.event.damage *= config.getDoubleFromExpressionDebug("multiplier", data)
            return true
        }
        return true
    }
}
