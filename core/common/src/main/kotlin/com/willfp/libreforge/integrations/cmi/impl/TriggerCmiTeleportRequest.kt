package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIPlayerTeleportRequestEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCmiTeleportRequest : Trigger("cmi_teleport_request") {
    override val description = "Fires when the player sends a teleport request to another player."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The player who received the request.",
        TriggerParameter.LOCATION to "The requesting player's location.",
        TriggerParameter.TEXT to "The type of request, e.g. tpa, tpahere, tpaall."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CMIPlayerTeleportRequestEvent) {
        val player = event.whoOffers ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = event.whoAccepts,
                location = player.location,
                text = event.action?.name
            )
        )
    }
}
