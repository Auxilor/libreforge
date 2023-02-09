package com.willfp.libreforge.triggers.triggers

import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerBucketFillEvent

class TriggerFillBucket: Trigger(
    "fill_bucket", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerBucketFillEvent) {
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