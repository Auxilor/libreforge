package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent

object TriggerCatchFish : Trigger("catch_fish") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return

        val player = event.player
        val caught = event.caught as? Item ?: return

        val caughtStack = caught.itemStack

        val editableEvent = EditableDropEvent(
            initialDrops = listOf(caughtStack),
            cause = DropCause.FISHING,
            context = DropContext(
                player = player,
                tool = player.inventory.itemInMainHand
            ),
            dropLocation = event.hook.location,
            cancellable = event
        )

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = editableEvent.dropLocation,
                event = editableEvent,
                item = caughtStack,
                value = event.expToDrop.toDouble()
            )
        )

        val dropResults = editableEvent.items

        if (editableEvent.drops.isEmpty()) {
            caught.remove()
        } else {
            caught.itemStack = editableEvent.drops.first()

            for (extra in editableEvent.drops.drop(1)) {
                caught.world.dropItemNaturally(caught.location, extra)
            }
        }

        event.expToDrop += dropResults.sumOf { it.xp }
    }
}
