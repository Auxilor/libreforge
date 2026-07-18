package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.husktowns.events.PlayerLeaveTownEvent
import org.bukkit.event.EventHandler

object TriggerExitClaim : Trigger("exit_claim") {
    override val description = "Fires when the player exits a HuskTowns town claim."

    override val categories = setOf("movement")

    override val additionalInfo = listOf("Requires HuskTowns to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The name of the town whose claim was exited."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerLeaveTownEvent) {
        val player = event.player ?: return
        val town = event.leftTownClaim.town.name ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                text = town
            )
        )
    }
}