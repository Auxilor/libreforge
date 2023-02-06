package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

object TriggerTridentAttack : Trigger("trident_attack") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.PROJECTILE,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val trident = event.damager
        val victim = event.entity

        if (trident !is Trident) {
            return
        }

        if (victim !is LivingEntity) {
            return
        }

        val shooter = trident.shooter

        if (shooter !is Player) {
            return
        }

        if (event.isCancelled) {
            return
        }

        this.dispatch(
            shooter,
            TriggerData(
                player = shooter,
                victim = victim,
                projectile = trident,
                location = trident.location,
                event = event,
                item = trident.item,
                velocity = trident.velocity,
                value = event.finalDamage
            )
        )
    }
}
