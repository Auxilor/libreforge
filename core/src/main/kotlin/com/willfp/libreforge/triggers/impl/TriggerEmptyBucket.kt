package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerBucketFillEvent

object TriggerEmptyBucket : Trigger("empty_bucket") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerBucketFillEvent) {
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                event = event
            )
        )
    }
}
