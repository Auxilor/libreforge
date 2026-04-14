package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.holders
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent

private const val META_KEY = "libreforge_trident_hit_holders"

object TriggerTridentHit : Trigger("trident_hit") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.PROJECTILE,
        TriggerParameter.LOCATION,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        val trident = event.entity as? Trident ?: return
        val shooter = trident.shooter as? LivingEntity ?: return

        trident.setMetadata(
            META_KEY,
            plugin.metadataValueFactory.create(shooter.toDispatcher().holders)
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ProjectileHitEvent) {
        val trident = event.entity as? Trident ?: return
        val shooter = trident.shooter as? LivingEntity ?: return

        @Suppress("UNCHECKED_CAST")
        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter as? Player,
                projectile = trident,
                location = trident.location,
                block = event.hitBlock,
                event = event,
                velocity = trident.velocity
            ),
            forceHolders = trident.getMetadata(META_KEY).firstOrNull()?.value() as? Collection<ProvidedHolder>
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageByEntityEvent) {
        val trident = event.damager as? Trident ?: return
        val victim = event.entity as? LivingEntity ?: return
        val shooter = trident.shooter as? LivingEntity ?: return

        @Suppress("UNCHECKED_CAST")
        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter as? Player,
                victim = victim,
                location = trident.location,
                event = event,
                velocity = trident.velocity,
                projectile = trident
            ),
            forceHolders = trident.getMetadata(META_KEY).firstOrNull()?.value() as? Collection<ProvidedHolder>
        )
    }
}
