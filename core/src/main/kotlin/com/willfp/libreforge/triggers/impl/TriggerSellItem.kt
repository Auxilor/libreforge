package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object TriggerSellItem : Trigger("sell_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handle(event: ShopSellEvent) {
        val player = event.player
        val item = event.item

        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                item = item,
                value = event.value.getValue(player, event.multiplier)
            )
        )
    }
}
