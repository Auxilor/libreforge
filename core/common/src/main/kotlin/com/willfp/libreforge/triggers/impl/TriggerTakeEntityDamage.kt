package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.tryAsLivingEntity
import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.Bukkit
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
        val attacker = event.damager.tryAsLivingEntity() ?: return

        val victim = event.entity

        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            if (MythicBukkit.inst().mobManager.isMythicMob(attacker)) {
                return
            }
        }

        if (event.cause == EntityDamageEvent.DamageCause.THORNS) {
            return
        }

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