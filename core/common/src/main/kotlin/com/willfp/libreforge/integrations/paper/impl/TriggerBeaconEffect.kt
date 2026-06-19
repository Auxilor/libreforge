package com.willfp.libreforge.integrations.paper.impl

import com.destroystokyo.paper.event.block.BeaconEffectEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerBeaconEffect : Trigger("beacon_effect") {
    override val description = "Fires when the player receives a potion effect from a beacon."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires Paper to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BeaconEffectEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event
            )
        )
    }
}
