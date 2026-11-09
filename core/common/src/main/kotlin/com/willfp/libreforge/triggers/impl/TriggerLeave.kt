package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object TriggerLeave : Trigger("leave") {
    override val description = "Fires when the player leaves the server."

    override val categories = setOf("player")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location on leaving."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    /**
     * Dispatched by the quit cleanup before the player's effects are disabled.
     */
    internal fun dispatchLeave(player: Player) {
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location
            )
        )
    }
}
