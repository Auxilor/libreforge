package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class TriggerTakeEntityDamage : Trigger(
    "take_entity_damage", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val attacker = event.damager

        if (attacker !is LivingEntity) {
            return
        }

        val victim = event.entity

        if (victim !is Player) {
            return
        }

        if (event.isCancelled) {
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.THORNS) {
            return
        }

        this.processTrigger(
            victim,
            TriggerData(
                player = victim,
                victim = attacker,
                location = attacker.location,
                event = WrappedDamageEvent(event)
            ),
            event.finalDamage
        )
    }
}
