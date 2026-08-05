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
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object TriggerBowAttack : Trigger("bow_attack") {
    override val description = "Fires when the player hits an entity with an arrow."

    override val categories = setOf("combat")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was hit by the arrow.",
        TriggerParameter.LOCATION to "The victim's location at impact.",
        TriggerParameter.VELOCITY to "The velocity of the arrow at impact.",
        TriggerParameter.PROJECTILE to "The arrow that hit the entity.",
        TriggerParameter.VALUE to "The damage dealt.",
        TriggerParameter.ALT_VALUE to "The base damage, before armour, resistance, or other modifiers."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.PROJECTILE,
        TriggerParameter.VELOCITY,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE
    )
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handle(event: EntityDamageByEntityEvent) {
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
                value = event.finalDamage,
                altValue = event.getDamage(EntityDamageEvent.DamageModifier.BASE)
            )
        )
    }
}
