package com.willfp.libreforge.internal

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
    fun onBlockBreak(event: BlockBreakEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player
        val block = event.block
        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        for (holder in player.getHolders()) {
            for ((effect, config) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }
                val aEvent = EffectActivateEvent(player, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    effect.onBlockBreak(player, block, event, config)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onArrowDamage(event: EntityDamageByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val arrow = event.damager
        val victim = event.entity

        if (arrow !is Arrow) {
            return
        }

        if (victim !is LivingEntity) {
            return
        }

        val shooter = arrow.shooter

        if (shooter !is Player) {
            return
        }

        if (!AntigriefManager.canInjure(shooter, victim)) {
            return
        }

        if (event.isCancelled) {
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
                    effect.onArrowDamage(shooter, victim, arrow, event, config)
                    effect.onAnyDamage(shooter, victim, event, config)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onTridentDamage(event: EntityDamageByEntityEvent) {
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

        if (!AntigriefManager.canInjure(shooter, victim)) {
            return
        }

        if (event.isCancelled) {
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
                    effect.onTridentDamage(shooter, victim, trident, event, config)
                    effect.onAnyDamage(shooter, victim, event, config)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onJump(event: PlayerJumpEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }
        val player = event.player

        for (holder in player.getHolders()) {
            for ((effect, config) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }
                val aEvent = EffectActivateEvent(player, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    effect.onJump(player, event, config)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onMeleeAttack(event: EntityDamageByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val attacker = event.damager

        if (attacker !is Player) {
            return
        }

        val victim = event.entity

        if (victim !is LivingEntity) {
            return
        }

        if (event.isCancelled) {
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.THORNS) {
            return
        }

        if (!AntigriefManager.canInjure(attacker, victim)) {
            return
        }

        for (holder in attacker.getHolders()) {
            for ((effect, config) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }
                val aEvent = EffectActivateEvent(attacker, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    effect.onMeleeAttack(attacker, victim, event, config)
                    effect.onAnyDamage(attacker, victim, event, config)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onKill(event: EntityDeathByEntityEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        var killer: Any? = null
        if (event.killer is Player) {
            killer = event.killer
        } else if (event.killer is Projectile) {
            if ((event.killer as Projectile).shooter is Player) {
                killer = (event.killer as Projectile).shooter
            }
        }

        if (killer !is Player) {
            return
        }

        val victim = event.victim

        if (!AntigriefManager.canInjure(killer, victim)) {
            return
        }

        for (holder in killer.getHolders()) {
            for ((effect, config) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }
                val aEvent = EffectActivateEvent(killer, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    (effect as Watcher).onKill(killer, victim, event, config)
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val shooter = event.entity.shooter

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
                    effect.onProjectileLaunch(shooter, event.entity, event, config)
                }
            }
        }
    }

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
                    effect.onDamageWearingArmor(victim, event, config)
                }
            }
        }
    }
}