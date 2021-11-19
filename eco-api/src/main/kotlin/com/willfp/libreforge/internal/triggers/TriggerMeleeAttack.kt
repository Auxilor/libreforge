package com.willfp.libreforge.internal.triggers

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.api.events.EffectActivateEvent
import com.willfp.libreforge.api.getHolders
import com.willfp.libreforge.api.triggers.Trigger
import com.willfp.libreforge.api.triggers.TriggerData
import com.willfp.libreforge.api.triggers.wrappers.WrappedDamageEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class TriggerMeleeAttack: Trigger("melee_attack") {
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

                val activateEvent = EffectActivateEvent(attacker, holder, effect)
                this.plugin.server.pluginManager.callEvent(activateEvent)

                if (!activateEvent.isCancelled) {
                    effect.handle(
                        TriggerData(
                            player = attacker,
                            victim = victim,
                            event = WrappedDamageEvent(event)
                        ), config
                    )
                }
            }
        }
    }
}