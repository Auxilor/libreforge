package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class TriggerBowAttack : Trigger("bow_attack") {
    @EventHandler(ignoreCancelled = true)
    fun onArrowDamage(event: EntityDamageByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val arrow = event.damager
        val victim = event.entity

        if (arrow !is Arrow) {
            return
        }

        if (victim !is LivingEntity) {
            return
        }

        val shooter = arrow.shooter

        if (shooter !is Player) {
            return
        }

        if (!AntigriefManager.canInjure(shooter, victim)) {
            return
        }

        if (event.isCancelled) {
            return
        }

        this.processTrigger(
            shooter,
            TriggerData(
                player = shooter,
                victim = victim,
                event = WrappedDamageEvent(event)
            )
        )
    }
}