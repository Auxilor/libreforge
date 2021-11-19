package com.willfp.libreforge.internal.triggers

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.api.events.EffectActivateEvent
import com.willfp.libreforge.api.getHolders
import com.willfp.libreforge.api.triggers.Trigger
import com.willfp.libreforge.api.triggers.TriggerData
import com.willfp.libreforge.api.triggers.wrappers.WrappedDamageEvent
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class TriggerBowAttack : Trigger("bow_attack") {
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
            for ((effect, config, filter, triggers) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }

                if (!triggers.contains(this)) {
                    continue
                }

                if (!filter.matches(victim)) {
                    continue
                }

                val aEvent = EffectActivateEvent(shooter, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    effect.handle(
                        TriggerData(
                            player = shooter,
                            victim = victim,
                            event = WrappedDamageEvent(event)
                        ), config
                    )
                }
            }
        }
    }
}