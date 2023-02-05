package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

class TriggerFallDamage : Trigger(
    "fall_damage", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        if (event.cause != EntityDamageEvent.DamageCause.FALL) {
            return
        }

        val victim = event.entity

        if (victim !is Player) {
            return
        }

        this.processTrigger(
            victim,
            TriggerData(
                player = victim,
                location = victim.location,
                event = WrappedDamageEvent(event)
            ),
            event.finalDamage
        )
    }
}
