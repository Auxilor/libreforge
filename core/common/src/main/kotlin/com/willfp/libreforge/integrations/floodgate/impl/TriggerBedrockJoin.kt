package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

object TriggerBedrockJoin : Trigger("bedrock_join") {
    override val description = "Fires when a player joins the server from Bedrock edition."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Fires for linked Bedrock players too, even though they join under their Java account's UUID."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location on joining."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler
    fun handle(event: PlayerJoinEvent) {
        if (!isBedrockPlayer(event.player)) {
            return
        }

        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                location = event.player.location
            )
        )
    }
}
