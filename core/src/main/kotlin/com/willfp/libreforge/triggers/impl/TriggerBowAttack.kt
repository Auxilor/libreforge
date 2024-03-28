package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object TriggerBowAttack : Trigger("bow_attack") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VELOCITY,
        TriggerParameter.PROJECTILE
    )
    
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (!isEnabled) return

        val arrow = event.damager
        val victim = event.entity as? LivingEntity ?: return

        if (arrow !is AbstractArrow || arrow is Trident) {
            return
        }

        val shooter = arrow.shooter as? LivingEntity ?: return

        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter as? Player,
                victim = victim,
                location = victim.location,
                event = event,
                velocity = arrow.velocity,
                projectile = arrow,
                value = event.finalDamage
            )
        )
    }
}
