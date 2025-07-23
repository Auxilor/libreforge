package com.willfp.libreforge.integrations.axplugins.axtrade.impl

import com.artillexstudios.axtrade.api.events.AxTradeCompleteEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerPlayerTrade : Trigger("player_trade") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: AxTradeCompleteEvent) {
        val player = event.trade.player2.player
        val victim = event.trade.player1.player

        val tradedItems = (event.trade.player1.tradeGui.getItems(false) + event.trade.player2.tradeGui.getItems(false)).filterNotNull()
            .groupBy { it.type }
            .mapValues { (_, stacks) ->
                stacks.reduce { acc, item ->
                    acc.clone().apply { amount += item.amount }
                }
            }

        val mergedItem = tradedItems.values.firstOrNull()
        val totalAmount = tradedItems.values.sumOf { it.amount }

        val totalCurrency = sumCurrencies(event)

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = victim,
                item = mergedItem,
                value = totalAmount.toDouble(),
                altValue = totalCurrency
            )
        )
    }

    private fun sumCurrencies(event: AxTradeCompleteEvent): Double {
        val player1Total = event.trade.player1.currencies.values.sum()
        val player2Total = event.trade.player2.currencies.values.sum()
        return player1Total + player2Total
    }
}
