package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent

object TriggerProjectileHit : Trigger("projectile_hit") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.PROJECTILE,
        TriggerParameter.LOCATION,
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
                event = event,
                velocity = projectile.velocity
            )
        )
    }
}
