package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent

class TriggerProjectileLaunch : Trigger(
    "projectile_launch", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.PROJECTILE
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
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
                projectile = event.entity
            )
        )
    }
}
