package com.willfp.libreforge.integrations.shopkeepers.impl

import com.nisovin.shopkeepers.api.events.ShopkeeperTradeCompletedEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerShopkeepersTrade : Trigger("shopkeepers_trade") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE
    )

    @EventHandler
    fun handle(event: ShopkeeperTradeCompletedEvent) {
        val trade = event.completedTrade
        val player = trade.player
        val resultItem = trade.tradingRecipe.resultItem // never null
        val location = event.shopkeeper.location ?: player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                item = resultItem.copy(),
                value = resultItem.amount.toDouble(),
                altValue = 1.0
            )
        )
    }
}
