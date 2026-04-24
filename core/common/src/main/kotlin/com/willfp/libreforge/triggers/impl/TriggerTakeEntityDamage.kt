package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.integrations.mythicmobs.utils.isMythicMob
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.tryAsLivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object TriggerTakeEntityDamage : Trigger("take_entity_damage") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.THORNS) return
        val attacker = event.damager.tryAsLivingEntity() ?: return
        val victim = event.entity
        // If attacker is MythicMob, then skip, use 'take_mythic_damage' instead.
        if (attacker.isMythicMob()) return
        this.dispatch(
            victim.toDispatcher(),
            TriggerData(
                player = victim as? Player,
                victim = attacker,
                location = attacker.location,
                event = event,
                value = event.finalDamage
            )
        )
    }
}