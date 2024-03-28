package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent

object TriggerShootBow : Trigger("shoot_bow") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.PROJECTILE,
        TriggerParameter.EVENT,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityShootBowEvent) {
        if (!isEnabled) return
        val shooter = event.entity as? LivingEntity ?: return

        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter as? Player,
                victim = shooter,
                projectile = event.projectile as? Projectile,
                event = event,
                velocity = event.projectile.velocity,
                value = event.force.toDouble()
            )
        )
    }
}
