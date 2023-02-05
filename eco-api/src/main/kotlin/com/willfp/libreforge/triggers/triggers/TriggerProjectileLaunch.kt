package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent

class TriggerProjectileLaunch : Trigger(
    "projectile_launch", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.PROJECTILE,
        TriggerParameter.VELOCITY,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: ProjectileLaunchEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val shooter = event.entity.shooter

        if (shooter !is Player) {
            return
        }

        this.processTrigger(
            shooter,
            TriggerData(
                player = shooter,
                projectile = event.entity,
                velocity = event.entity.velocity,
                event = GenericCancellableEvent(event),
                location = event.entity.location
            )
        )
    }
}
