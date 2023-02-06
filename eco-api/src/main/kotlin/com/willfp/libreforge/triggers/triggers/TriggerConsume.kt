package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent

class TriggerConsume : Trigger(
    "consume", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerItemConsumeEvent) {
        val player = event.player
        this.processTrigger(
            player,
            TriggerData(
                player = player,
                event = GenericCancellableEvent(event),
                item = event.item
            )
        )
    }
}
