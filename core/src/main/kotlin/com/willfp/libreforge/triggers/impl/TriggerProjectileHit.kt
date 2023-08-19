package com.willfp.libreforge.triggers.impl

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
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.PROJECTILE,
        TriggerParameter.LOCATION,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ProjectileHitEvent) {
        val projectile = event.entity
        val shooter = projectile.shooter

        if (shooter !is Player) {
            return
        }

        this.dispatch(
            shooter,
            TriggerData(
                player = shooter,
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
        val player = arrow.shooter as? Player ?: return

        this.dispatch(
            player,
            TriggerData(
                player = player,
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
