package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class TriggerSwapHands : Trigger(
    "swap_hands", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSwapHandItemsEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player
        if (event.isCancelled) {
            return
        }

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                item = event.mainHandItem
            ),
        )
    }
}
