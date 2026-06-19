package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent

object TriggerProjectileLaunch : Trigger("projectile_launch") {
    override val description = "Fires when the player launches a projectile."

    override val categories = setOf("combat")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that launched the projectile (same as the trigger source).",
        TriggerParameter.PROJECTILE to "The projectile that was launched.",
        TriggerParameter.LOCATION to "The launch location of the projectile.",
        TriggerParameter.VELOCITY to "The initial velocity of the launched projectile."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.PROJECTILE,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ProjectileLaunchEvent) {
        val shooter = event.entity.shooter as? LivingEntity ?: return

        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter as? Player,
                victim = shooter,
                projectile = event.entity,
                velocity = event.entity.velocity,
                event = event,
                location = event.entity.location
            )
        )
    }
}
