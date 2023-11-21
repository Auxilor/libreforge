package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object TriggerShieldBlock : Trigger("shield_block") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
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

        this.dispatch(
            PlayerDispatcher(victim),
            TriggerData(
                player = victim,
                victim = attacker,
                location = attacker.location,
                event = event,
                value = event.damage
            )
        )
    }
}
