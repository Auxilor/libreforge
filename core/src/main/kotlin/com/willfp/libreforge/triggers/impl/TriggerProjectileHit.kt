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
        if (!isEnabled) return
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
        if (!isEnabled) return
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
