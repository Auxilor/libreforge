package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.holders
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import java.util.WeakHashMap


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
    private val map = mutableMapOf<Trident, Collection<ProvidedHolder>>()

    @EventHandler(ignoreCancelled = true)
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        val shooter = event.entity.shooter as? Player ?: return
        val trident = event.entity

        if (trident !is Trident) {
            return
        }

        map[trident] = shooter.holders
        plugin.scheduler.runLater(6_000L) { //5 minutes
            map.remove(trident)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val trident = event.damager as? Trident ?: return
        val victim = event.entity as? LivingEntity ?: return

        val shooter = trident.shooter as? Player ?: return

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
            ),
            forceHolders = map[trident]
        )
    }
}
