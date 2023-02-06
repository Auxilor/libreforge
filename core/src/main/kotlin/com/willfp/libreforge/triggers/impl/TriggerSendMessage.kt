@file:Suppress("DEPRECATION")

package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent

object TriggerSendMessage : Trigger("send_message") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: AsyncPlayerChatEvent) {
        val player = event.player
        if (event.isCancelled) {
            return
        }

        plugin.scheduler.run {
            this.dispatch(
                player,
                TriggerData(
                    player = player,
                    location = player.location,
                    text = event.message
                )
            )
        }
    }
}
