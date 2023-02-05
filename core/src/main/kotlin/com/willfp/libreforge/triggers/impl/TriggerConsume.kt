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
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player
        this.dispatch(
            player,
            TriggerData(
                player = player,
                event = event,
                item = event.item
            )
        )
    }
}
