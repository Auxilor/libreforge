package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object TriggerMeleeAttack : Trigger("melee_attack") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (!isEnabled) return
        val attacker = event.damager as? LivingEntity ?: return
        val victim = event.entity as? LivingEntity ?: return

        if (event.cause == EntityDamageEvent.DamageCause.THORNS) {
            return
        }

        this.dispatch(
            attacker.toDispatcher(),
            TriggerData(
                player = attacker as? Player,
                victim = victim,
                location = victim.location,
                event = event,
                item = attacker.equipment?.itemInMainHand,
                value = event.finalDamage
            )
        )
    }
}
