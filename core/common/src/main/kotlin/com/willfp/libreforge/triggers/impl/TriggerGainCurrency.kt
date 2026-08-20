package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerGainCurrency : Trigger("gain_currency") {
    override val description = "Fires when the player gains currency, such as an EcoBits currency " +
        "or Vault money."

    override val categories = setOf("economy")

    override val additionalInfo = listOf(
        "Requires either EcoBits or a Vault-compatible economy plugin to be installed."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.VALUE to "The amount of currency gained.",
        TriggerParameter.ALT_VALUE to "The player's new balance after the gain.",
        TriggerParameter.TEXT to "The id of the currency gained, or \"vault\" for Vault money."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE,
        TriggerParameter.TEXT
    )
}
