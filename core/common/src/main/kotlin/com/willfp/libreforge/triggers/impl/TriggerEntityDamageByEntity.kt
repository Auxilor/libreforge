package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.lumine.mythic.bukkit.events.MythicDamageEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

object TriggerEntityDamageByEntity : Trigger("entity_damage_by_entity") {
    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        val damager = event.damager as? LivingEntity ?: return

        this.dispatch(
            entity.toDispatcher(),
            TriggerData(
                victim = damager,
                location = entity.location,
                value = event.finalDamage
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: MythicDamageEvent){
        val damager = event.caster.entity.bukkitEntity?:return
        val livingAttacker = damager as? LivingEntity ?: return
        val victim = event.target.bukkitEntity?:return

        this.dispatch(
            victim.toDispatcher(),
            TriggerData(
                victim = livingAttacker,
                location = victim.location,
                value = event.damage
            )
        )
    }
}
