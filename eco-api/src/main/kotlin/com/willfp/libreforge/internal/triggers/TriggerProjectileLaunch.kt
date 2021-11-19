package com.willfp.libreforge.internal.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.api.events.EffectActivateEvent
import com.willfp.libreforge.api.getHolders
import com.willfp.libreforge.api.triggers.Trigger
import com.willfp.libreforge.api.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent

class TriggerProjectileLaunch : Trigger("projectile_launch") {
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
            for ((effect, config, filter, triggers) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }

                if (!triggers.contains(this)) {
                    continue
                }

                val aEvent = EffectActivateEvent(shooter, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)

                if (!aEvent.isCancelled) {
                    effect.handle(
                        TriggerData(
                            player = shooter,
                            projectile = event.entity
                        ), config
                    )
                }
            }
        }
    }
}
