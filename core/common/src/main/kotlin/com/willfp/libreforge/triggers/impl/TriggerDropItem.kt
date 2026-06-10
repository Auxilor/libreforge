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
    override val description = "Fires when the player drops an item from their inventory."

    override val categories = setOf("inventory")

    override val parameterDescriptions = mapOf(
        TriggerParameter.ITEM to "The item that was dropped.",
        TriggerParameter.LOCATION to "The location where the item was dropped.",
        TriggerParameter.VALUE to "The number of items dropped."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
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
