package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

object TriggerChangeChunk : Trigger("change_chunk") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VELOCITY,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        if (event.to.chunk.x != event.from.chunk.x
            || event.to.chunk.z != event.from.chunk.z
        ) {
            return
        }

        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                location = event.to,
                velocity = player.velocity,
                event = event,
                item = player.inventory.itemInMainHand
            )
        )
    }
}
