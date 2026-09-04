package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIUserHomeRemoveEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCmiHomeRemove : Trigger("cmi_home_remove") {
    override val description = "Fires when the player deletes a home."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The location of the home.",
        TriggerParameter.TEXT to "The name of the home."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CMIUserHomeRemoveEvent) {
        val player = event.user?.player ?: return
        val home = event.home ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = home.loc?.bukkitLoc,
                text = home.name
            )
        )
    }
}
