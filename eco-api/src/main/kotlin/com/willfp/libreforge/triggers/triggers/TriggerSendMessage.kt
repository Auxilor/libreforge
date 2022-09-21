package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent

class TriggerSendMessage : Trigger(
    "send_message", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )
) {
    @EventHandler(ignoreCancelled = true)
    @Suppress("DEPRECATION")
    fun handle(event: AsyncPlayerChatEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player
        if (event.isCancelled) {
            return
        }

        this.plugin.scheduler.run {
            this.processTrigger(
                player,
                TriggerData(
                    player = player,
                    location = player.location,
                    text = event.message
                ),
            )
        }
    }
}
