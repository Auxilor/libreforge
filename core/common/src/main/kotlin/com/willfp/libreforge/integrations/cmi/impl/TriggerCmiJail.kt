package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIPlayerJailEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCmiJail : Trigger("cmi_jail") {
    override val description = "Fires when the player is jailed."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.TEXT to "The name of the jail the player was sent to."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CMIPlayerJailEvent) {
        val player = event.user?.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                text = event.cell?.jail?.name
            )
        )
    }
}
