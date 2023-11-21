package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerDropItemEvent

object TriggerDropItem : Trigger("drop_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerDropItemEvent) {
        val player = event.player

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                item = event.itemDrop.itemStack,
                value = event.itemDrop.itemStack.amount.toDouble(),
                event = event
            )
        )
    }
}
