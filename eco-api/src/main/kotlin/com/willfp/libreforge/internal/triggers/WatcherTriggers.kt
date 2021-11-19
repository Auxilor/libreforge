package com.willfp.libreforge.internal.triggers

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.events.PlayerJumpEvent
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.api.Watcher
import com.willfp.libreforge.api.events.EffectActivateEvent
import com.willfp.libreforge.api.getHolders
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent

internal class WatcherTriggers(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onFallDamage(event: EntityDamageEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        if (event.cause != EntityDamageEvent.DamageCause.FALL) {
            return
        }

        val victim = event.entity

        if (victim !is Player) {
            return
        }

        for (holder in victim.getHolders()) {
            for ((effect, config) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }
                val aEvent = EffectActivateEvent(victim, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    effect.onFallDamage(victim, event, config)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onProjectileHit(event: ProjectileHitEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val projectile = event.entity
        val shooter = projectile.shooter

        if (shooter !is Player) {
            return
        }

        for (holder in shooter.getHolders()) {
            for ((effect, config) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }
                val aEvent = EffectActivateEvent(shooter, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    effect.onProjectileHit(shooter, event, config)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onDamageWearingArmor(event: EntityDamageEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val victim = event.entity

        if (victim !is Player) {
            return
        }

        for (holder in victim.getHolders()) {
            for ((effect, config) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }
                val aEvent = EffectActivateEvent(victim, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    effect.onIncomingDamage(victim, event, config)
                }
            }
        }
    }
}