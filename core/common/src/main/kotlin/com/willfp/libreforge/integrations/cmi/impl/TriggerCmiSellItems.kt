package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIPlayerItemsSellEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCmiSellItems : Trigger("cmi_sell_items") {
    override val description = "Fires when the player sells items through CMI."

    override val categories = setOf("economy", "player")

    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The total amount of money paid out.",
        TriggerParameter.ALT_VALUE to "The total number of items sold.",
        TriggerParameter.TEXT to "The sell type, one of all, hand, blocks, same, material, or gui."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler
    fun handle(event: CMIPlayerItemsSellEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                value = event.totalPayment,
                altValue = event.totalAmount.toDouble(),
                text = event.sellType?.name
            )
        )
    }
}
