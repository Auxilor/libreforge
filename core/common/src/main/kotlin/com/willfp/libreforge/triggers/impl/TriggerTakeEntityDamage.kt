package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.tryAsLivingEntity
import io.lumine.mythic.bukkit.events.MythicDamageEvent
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.atan

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

    @EventHandler(ignoreCancelled = true)
    fun handle(event: MythicDamageEvent){
        val attacker = event.caster.entity.bukkitEntity?:return
        val livingAttacker = attacker as? LivingEntity ?: return
        val victim = event.target.bukkitEntity?:return


        this.dispatch(
            victim.toDispatcher(),
            TriggerData(
                player = victim as? Player,
                victim = livingAttacker,
                location = livingAttacker.location,
                event = event,
                value = event.damage
            )
        )
    }
}
