package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableFishDropEvent
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

        val editableEvent = EditableFishDropEvent(
            cancellable = event,
            dropLocationSupplier = { event.hook.location },
            itemEntity = caught
        )

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = editableEvent.dropLocation,
                event = editableEvent,
                item = caught.itemStack,
                value = event.expToDrop.toDouble()
            )
        )

        event.expToDrop = editableEvent.items.sumOf { it.xp }
    }
}
