package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent

object TriggerProjectileHit : Trigger("projectile_hit") {
    override val description = "Fires when the player's projectile hits a block or entity."

    override val categories = setOf("combat")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity hit by the projectile, if any.",
        TriggerParameter.PROJECTILE to "The projectile that hit.",
        TriggerParameter.LOCATION to "The projectile's location at impact.",
        TriggerParameter.BLOCK to "The block hit by the projectile, if any.",
        TriggerParameter.VELOCITY to "The velocity of the projectile at impact."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.PROJECTILE,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ProjectileHitEvent) {
        val projectile = event.entity
        val shooter = projectile.shooter as? LivingEntity ?: return

        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter as? Player,
                projectile = projectile,
                location = projectile.location,
                block = event.hitBlock,
                event = event,
                velocity = projectile.velocity
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val arrow = event.damager as? Arrow ?: return
        val victim = event.entity as? LivingEntity ?: return
        val shooter = arrow.shooter as? LivingEntity ?: return

        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter as? Player,
                victim = victim,
                location = arrow.location,
                event = event,
                velocity = arrow.velocity,
                projectile = arrow
            )
        )
    }
}
