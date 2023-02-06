package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object TriggerSwapHands : Trigger("swap_hands") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSwapHandItemsEvent) {
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
