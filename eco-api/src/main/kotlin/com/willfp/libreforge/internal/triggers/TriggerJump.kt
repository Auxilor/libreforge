package com.willfp.libreforge.internal.triggers

import com.willfp.eco.core.events.PlayerJumpEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.api.events.EffectActivateEvent
import com.willfp.libreforge.api.getHolders
import com.willfp.libreforge.api.triggers.Trigger
import com.willfp.libreforge.api.triggers.TriggerData
import org.bukkit.event.EventHandler

class TriggerJump : Trigger("jump") {
    @EventHandler(ignoreCancelled = true)
    fun onJump(event: PlayerJumpEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }
        val player = event.player

        for (holder in player.getHolders()) {
            for ((effect, config, filter, triggers) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }

                if (!triggers.contains(this)) {
                    continue
                }

                val aEvent = EffectActivateEvent(player, holder, effect)
                this.plugin.server.pluginManager.callEvent(aEvent)
                if (!aEvent.isCancelled) {
                    effect.handle(
                        TriggerData(
                            player = player
                        ), config
                    )
                }
            }
        }
    }
}
