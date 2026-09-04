package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIAfkEnterEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerCmiAfkEnter : Trigger("cmi_afk_enter") {
    override val description = "Fires when the player goes AFK."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the CMI plugin."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.TEXT to "How the player went AFK, either auto or manual."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CMIAfkEnterEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                text = event.type?.name
            )
        )
    }
}
