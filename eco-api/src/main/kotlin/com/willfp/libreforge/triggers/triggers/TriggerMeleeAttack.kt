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

class TriggerMeleeAttack : Trigger(
    "melee_attack", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val attacker = event.damager as? Player ?: return
        val victim = event.entity as? LivingEntity ?: return

        if (event.isCancelled) {
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.THORNS) {
            return
        }

        if (!AntigriefManager.canInjure(attacker, victim)) {
            return
        }

        this.processTrigger(
            attacker,
            TriggerData(
                player = attacker,
                victim = victim,
                location = victim.location,
                event = WrappedDamageEvent(event),
                item = attacker.inventory.itemInMainHand
            ),
            event.finalDamage
        )
    }
}
