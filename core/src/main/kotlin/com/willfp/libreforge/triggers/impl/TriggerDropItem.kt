package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDropItemEvent

object TriggerDropItem : Trigger("drop_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDropItemEvent) {
        val player = event.entity as? Player ?: return

        this.dispatch(
            player,
            TriggerData(
                player = player,
                item = event.itemDrop.itemStack,
                value = event.itemDrop.itemStack.amount.toDouble()
            )
        )
    }
}
