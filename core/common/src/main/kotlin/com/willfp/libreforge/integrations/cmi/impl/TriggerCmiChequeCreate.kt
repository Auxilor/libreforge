package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIChequeCreationEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCmiChequeCreate : Trigger("cmi_cheque_create") {
    override val description = "Fires when the player writes a CMI cheque."

    override val categories = setOf("economy", "player")

    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The value written on the cheque.",
        TriggerParameter.ITEM to "The cheque item."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CMIChequeCreationEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                value = event.price,
                item = event.cheque
            )
        )
    }
}
