package com.willfp.libreforge.integrations.ecobits.impl

import com.willfp.ecobits.events.CurrencyGainEvent
import com.willfp.ecobits.events.VaultBalanceGainEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerGainCurrency : Trigger("gain_currency") {
    override val description = "Fires when the player gains balance in any EcoBits currency, " +
        "or regular Vault money (whether EcoBits or another plugin is the registered Vault economy provider)."

    override val categories = setOf("economy")

    override val additionalInfo = listOf("Requires EcoBits to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VALUE to "The amount of currency gained.",
        TriggerParameter.ALT_VALUE to "The player's new balance after the gain.",
        TriggerParameter.TEXT to "The id of the currency gained, or \"vault\" for a polled external Vault economy gain."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler
    fun handle(event: CurrencyGainEvent) {
        val player = event.player.player ?: return

        this.dispatch(
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

    @EventHandler
    fun handle(event: VaultBalanceGainEvent) {
        val player = event.player.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                value = event.amountGained.toDouble(),
                altValue = event.newBalance.toDouble(),
                text = "vault"
            )
        )
    }
}
