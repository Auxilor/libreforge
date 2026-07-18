package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object TriggerSellItem : Trigger("sell_item") {
    override val description = "Fires when the player sells an item to a shop."

    override val categories = setOf("economy")

    override val parameterDescriptions = mapOf(
        TriggerParameter.ITEM to "The item that was sold.",
        TriggerParameter.VALUE to "The total sell value of the items."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handle(event: ShopSellEvent) {
        val player = event.player
        val item = event.item

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = item,
                value = event.value.getValue(player, event.multiplier)
            )
        )
    }
}
