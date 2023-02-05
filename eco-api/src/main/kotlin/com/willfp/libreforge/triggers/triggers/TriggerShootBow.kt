package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedShootBowEvent
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent

class TriggerShootBow : Trigger(
    "shoot_bow", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.PROJECTILE,
        TriggerParameter.EVENT,
        TriggerParameter.VELOCITY
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityShootBowEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val shooter = event.entity

        if (shooter !is Player) {
            return
        }

        this.processTrigger(
            shooter,
            TriggerData(
                player = shooter,
                projectile = event.projectile as? Projectile,
                event = WrappedShootBowEvent(event),
                velocity = event.projectile.velocity
            )
        )
    }
}
