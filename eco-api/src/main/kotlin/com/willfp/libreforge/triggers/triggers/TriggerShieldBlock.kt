package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class TriggerShieldBlock : Trigger(
    "shield_block", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val attacker = event.damager as? LivingEntity ?: return
        val victim = event.entity as? Player ?: return

        if (event.isCancelled) {
            return
        }

        if (!victim.isBlocking) {
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.THORNS) {
            return
        }

        if (!AntigriefManager.canInjure(victim, attacker)) {
            return
        }

        this.processTrigger(
            victim,
            TriggerData(
                player = victim,
                victim = attacker,
                location = attacker.location,
                event = WrappedDamageEvent(event)
            )
        )
    }
}
