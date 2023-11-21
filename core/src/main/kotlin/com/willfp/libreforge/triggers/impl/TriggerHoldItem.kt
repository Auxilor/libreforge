package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemHeldEvent

object TriggerHoldItem : Trigger("hold_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerItemHeldEvent) {
        val player = event.player
        if (event.isCancelled) {
            return
        }

        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                item = event.player.inventory.getItem(event.newSlot)
            )
        )
    }
}
