package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.getAttachedHolders
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedDamageEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class TriggerTridentAttack : Trigger(
    "trident_attack", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.PROJECTILE,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM,
        TriggerParameter.VELOCITY
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

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

        this.processTrigger(
            shooter,
            TriggerData(
                player = shooter,
                victim = victim,
                projectile = trident,
                location = trident.location,
                event = WrappedDamageEvent(event),
                item = trident.item,
                velocity = trident.velocity
            ),
            event.finalDamage,
            trident.getAttachedHolders()
        )
    }
}
