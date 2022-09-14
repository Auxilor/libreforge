package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerSellItem : Trigger(
    "sell_item", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: ShopSellEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player
        val item = event.item

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                item = item
            ),
            value = event.price
        )
    }
}
