package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class TriggerHeadshot : Trigger(
    "headshot", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VELOCITY
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val projectile = event.damager as? Projectile ?: return
        val victim = event.entity as? LivingEntity ?: return
        val shooter = projectile.shooter as? Player ?: return

        // Filter out non-headshots
        if (projectile.location.y < victim.location.y + victim.eyeHeight - 0.22) {
            return
        }

        this.dispatch(
            shooter,
            TriggerData(
                player = shooter,
                victim = victim,
                location = victim.location,
                event = event,
                velocity = projectile.velocity,
                value = event.finalDamage
            )
        )
    }
}
