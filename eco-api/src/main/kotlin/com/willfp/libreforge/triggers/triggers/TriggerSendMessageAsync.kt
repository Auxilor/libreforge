package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent

class TriggerSendMessageAsync : Trigger(
    "send_message_async", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
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

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = GenericCancellableEvent(event),
                text = event.message
            ),
        )
    }
}
