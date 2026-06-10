package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent

object TriggerMoveItem : Trigger("move_item") {
    override val description = "Fires when a player clicks an item in an inventory."

    override val categories = setOf("inventory")

    override val parameterDescriptions = mapOf(
        TriggerParameter.ITEM to "The item that was clicked.",
        TriggerParameter.VALUE to "The stack size of the clicked item."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val item = event.currentItem ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = item,
                event = event,
                value = item.amount.toDouble()
            )
        )
    }
}
