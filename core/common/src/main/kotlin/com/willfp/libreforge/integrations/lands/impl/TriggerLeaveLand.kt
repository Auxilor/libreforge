package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.PlayerLeaveLandEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerLeaveLand : Trigger("leave_land") {
    override val description = "Fires when the player leaves a Lands land membership."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires Lands to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.TEXT to "The name of the land."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerLeaveLandEvent) {
        val leavingPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(leavingPlayer.uid) ?: return
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                text = event.land.name
            )
        )
    }
}