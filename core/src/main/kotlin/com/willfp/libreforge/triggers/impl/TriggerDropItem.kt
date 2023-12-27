package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditablePlayerDropEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerDropItemEvent

object TriggerDropItem : Trigger("drop_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerDropItemEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = event.itemDrop.itemStack,
                value = event.itemDrop.itemStack.amount.toDouble(),
                event = EditablePlayerDropEvent(event),
                location = event.itemDrop.location
            )
        )
    }
}
