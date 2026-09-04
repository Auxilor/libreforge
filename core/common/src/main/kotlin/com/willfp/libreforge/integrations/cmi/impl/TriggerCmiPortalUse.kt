package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIPortalUseEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCmiPortalUse : Trigger("cmi_portal_use") {
    override val description = "Fires when the player goes through a CMI portal."

    override val categories = setOf("player", "world")

    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.TEXT to "The name of the portal."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CMIPortalUseEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                text = event.portal?.name
            )
        )
    }
}
