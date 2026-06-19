package com.willfp.libreforge.integrations.shopkeepers.impl

import com.nisovin.shopkeepers.api.events.ShopkeeperTradeCompletedEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerShopkeepersTrade : Trigger("shopkeepers_trade") {
    override val description = "Fires when the player completes a trade with a Shopkeepers shopkeeper."

    override val categories = setOf("economy")

    override val additionalInfo = listOf("Requires Shopkeepers to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The shopkeeper's location.",
        TriggerParameter.ITEM to "The result item from the trade.",
        TriggerParameter.VALUE to "The stack size of the result item."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
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
                value = resultItem.amount.toDouble()
            )
        )
    }
}
