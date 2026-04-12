package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
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
        val droppedItem = event.itemDrop

        val editableEvent = EditableDropEvent(
            initialDrops = listOf(droppedItem.itemStack),
            cause = DropCause.CUSTOM,
            context = DropContext(player = player),
            dropLocation = droppedItem.location,
            cancellable = event
        )

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = droppedItem.itemStack,
                value = droppedItem.itemStack.amount.toDouble(),
                event = editableEvent,
                location = droppedItem.location
            )
        )

        if (editableEvent.drops.isEmpty()) {
            droppedItem.remove()
        }
    }
}
