package com.willfp.libreforge.integrations.ecobits.impl

import com.willfp.ecobits.events.CurrencyGainEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerGainCurrency
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object EcoBitsCurrencyGainListener : Listener {
    @EventHandler
    fun handle(event: CurrencyGainEvent) {
        val player = event.player.player ?: return

        TriggerGainCurrency.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                value = event.amountGained.toDouble(),
                altValue = event.newBalance.toDouble(),
                text = event.currency.id
            )
        )
    }
}
