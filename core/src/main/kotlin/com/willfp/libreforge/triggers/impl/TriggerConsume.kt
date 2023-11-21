package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent

object TriggerConsume : Trigger("consume") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerItemConsumeEvent) {
        val player = event.player
        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                item = event.item
            )
        )
    }
}
