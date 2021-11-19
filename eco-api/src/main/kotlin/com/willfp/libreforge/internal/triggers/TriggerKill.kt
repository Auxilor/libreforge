package com.willfp.libreforge.internal.triggers

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.api.events.EffectActivateEvent
import com.willfp.libreforge.api.getHolders
import com.willfp.libreforge.api.triggers.Trigger
import com.willfp.libreforge.api.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler

class TriggerKill : Trigger("kill") {
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

                val activateEvent = EffectActivateEvent(killer, holder, effect)
                this.plugin.server.pluginManager.callEvent(activateEvent)

                if (!activateEvent.isCancelled) {
                    effect.handle(
                        TriggerData(
                            player = killer,
                            victim = victim
                        ), config
                    )
                }
            }
        }
    }
}