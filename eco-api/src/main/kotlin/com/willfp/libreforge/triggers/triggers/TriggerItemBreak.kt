package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemBreakEvent

class TriggerItemBreak : Trigger(
    "item_break", listOf(
        TriggerParameter.PLAYER
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerItemBreakEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player
            )
        )
    }
}
