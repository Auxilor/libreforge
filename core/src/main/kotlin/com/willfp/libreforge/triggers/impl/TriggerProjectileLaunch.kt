package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent

object TriggerProjectileLaunch : Trigger("projectile_launch") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.PROJECTILE,
        TriggerParameter.VELOCITY,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ProjectileLaunchEvent) {
        val shooter = event.entity.shooter

        if (shooter !is Player) {
            return
        }

        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter,
                projectile = event.entity,
                velocity = event.entity.velocity,
                event = event,
                location = event.entity.location
            )
        )
    }
}
