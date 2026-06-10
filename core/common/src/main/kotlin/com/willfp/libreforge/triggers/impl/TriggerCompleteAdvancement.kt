package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAdvancementDoneEvent

object TriggerCompleteAdvancement : Trigger("complete_advancement") {
    override val description = "Fires when the player completes an advancement."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Does not fire for recipe unlocks."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location when the advancement was completed.",
        TriggerParameter.TEXT to "The namespaced key of the completed advancement."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerAdvancementDoneEvent) {
        if (event.advancement.key.key.startsWith("recipes/")) {
            return
        }

        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = event.player.location,
                event = event,
                text = event.advancement.key.key
            )
        )
    }
}
