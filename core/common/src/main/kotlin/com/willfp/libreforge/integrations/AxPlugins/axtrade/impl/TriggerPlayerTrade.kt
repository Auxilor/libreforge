package com.willfp.libreforge.integrations.AxPlugins.axtrade.impl

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
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: AxTradeCompleteEvent) {
        val player = event.trade.player2.player
        val victim = event.trade.player1.player
        val playeritems = event.trade.player1.tradeGui.getItems(false).filterNotNull()
        val victimitems = event.trade.player2.tradeGui.getItems(false).filterNotNull()

        val allItems = playeritems + victimitems
        val item = allItems.firstOrNull()

        val totalAmount = allItems.sumOf { it.amount }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = victim,
                item = item,
                value = totalAmount.toDouble()
            )
        )
    }
}
