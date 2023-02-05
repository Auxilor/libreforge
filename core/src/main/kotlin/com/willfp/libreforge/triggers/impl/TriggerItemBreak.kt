package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemBreakEvent

class TriggerItemBreak : Trigger(
    "item_break", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerItemBreakEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                item = event.brokenItem
            )
        )
    }
}
