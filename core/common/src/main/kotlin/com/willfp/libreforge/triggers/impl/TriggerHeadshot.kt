package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object TriggerHeadshot : Trigger("headshot") {
    override val description = "Fires when the player hits an entity in the head with a projectile."

    override val categories = setOf("combat")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was hit in the head.",
        TriggerParameter.LOCATION to "The victim's location at impact.",
        TriggerParameter.VELOCITY to "The velocity of the projectile at impact.",
        TriggerParameter.PROJECTILE to "The projectile that hit the target.",
        TriggerParameter.VALUE to "The damage dealt."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.PROJECTILE,
        TriggerParameter.VELOCITY,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val projectile = event.damager as? Projectile ?: return
        val victim = event.entity as? LivingEntity ?: return
        val shooter = projectile.shooter as? LivingEntity ?: return

        // Filter out non-headshots
        if (projectile.location.y < victim.location.y + victim.eyeHeight - 0.22) {
            return
        }

        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter as? Player,
                victim = victim,
                location = victim.location,
                event = event,
                velocity = projectile.velocity,
                value = event.finalDamage,
                projectile = projectile
            )
        )
    }
}
