package com.willfp.libreforge.triggers.triggers

import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerBucketEmptyEvent

class TriggerEmptyBucket: Trigger(
    "empty_bucket", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerBucketEmptyEvent) {
        this.processTrigger(
            event.player,
            TriggerData(
                player = event.player,
                location = event.blockClicked.location,
                event = GenericCancellableEvent(event),
                item = event.itemStack
            )
        )
    }

}