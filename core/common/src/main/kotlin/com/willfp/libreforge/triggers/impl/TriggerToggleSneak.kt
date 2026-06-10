package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerToggleSneakEvent

object TriggerToggleSneak : Trigger("toggle_sneak") {
    override val description = "Fires when the player toggles sneaking."

    override val categories = setOf("movement")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VELOCITY to "The player's velocity at the time of the toggle."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerToggleSneakEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                velocity = player.velocity
            )
        )
    }
}
