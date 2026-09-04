package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIAfkLeaveEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCmiAfkLeave : Trigger("cmi_afk_leave") {
    override val description = "Fires when the player stops being AFK."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "The value is the number of seconds the player was AFK for."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The number of seconds the player was AFK for.",
        TriggerParameter.TEXT to "The reason the player stopped being AFK."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler
    fun handle(event: CMIAfkLeaveEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                value = event.time / 1000.0,
                text = event.reason
            )
        )
    }
}
