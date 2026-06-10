package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.LandTrustPlayerEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerJoinLand : Trigger("join_land") {
    override val description = "Fires when the player is trusted into a Lands land by another player."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires Lands to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The land owner who performed the trust action.",
        TriggerParameter.LOCATION to "The trusted player's location.",
        TriggerParameter.TEXT to "The name of the land."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandTrustPlayerEvent) {
        val trustedPlayer = event.targetUUID ?: return
        val player = Bukkit.getPlayer(trustedPlayer) ?: return
        val trustingPlayer = event.landPlayer ?: return
        val trustee = Bukkit.getPlayer(trustingPlayer.uid) ?: return
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                victim = trustee,
                text = event.land.name
            )
        )
    }
}