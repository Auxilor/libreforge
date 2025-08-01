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
        val player1 = event.trade.player1.player
        val player2 = event.trade.player2.player

        val tradedItems = (event.trade.player1.tradeGui.getItems(false) + event.trade.player2.tradeGui.getItems(false))
            .filterNotNull()
            .groupBy { it.type }
            .mapValues { (_, stacks) ->
                stacks.reduce { acc, item ->
                    acc.clone().apply { amount += item.amount }
                }
            }

        val mergedItem = tradedItems.values.firstOrNull()
        val totalAmount = tradedItems.values.sumOf { it.amount }

        val totalCurrency = sumCurrencies(event)

        // Kept victim logic so that effects can be run based on victim_conditions.
        // Could allow for more complex effect systems where affects are run if player has perm, and victim has perm.
        this.dispatch(
            player1.toDispatcher(),
            TriggerData(
                player = player1,
                victim = player2,
                item = mergedItem,
                value = totalAmount.toDouble(),
                altValue = totalCurrency
            )
        )

        this.dispatch(
            player2.toDispatcher(),
            TriggerData(
                player = player2,
                victim = player1,
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
