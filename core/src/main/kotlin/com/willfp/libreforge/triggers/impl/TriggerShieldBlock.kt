package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object TriggerShieldBlock : Trigger("shield_block") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (!isEnabled) return
        val attacker = event.damager as? LivingEntity ?: return
        val victim = event.entity as? LivingEntity ?: return

        if (event.isCancelled) {
            return
        }
        if (event.cause == EntityDamageEvent.DamageCause.THORNS) {
            return
        }

        this.dispatch(
            victim.toDispatcher(),
            TriggerData(
                player = victim as? Player,
                victim = attacker,
                location = attacker.location,
                event = event,
                value = event.damage
            )
        )
    }
}
