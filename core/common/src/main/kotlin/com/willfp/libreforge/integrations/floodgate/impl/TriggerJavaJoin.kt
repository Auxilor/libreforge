package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

object TriggerJavaJoin : Trigger("java_join") {
    override val description = "Fires when a player joins the server from Java edition, rather than through Geyser."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Does not fire for linked Bedrock players, who are on Bedrock despite using a Java account.",
        "Use the plain join trigger if you want every player regardless of edition."
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
        if (isBedrockPlayer(event.player)) {
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
