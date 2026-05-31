package com.willfp.libreforge.integrations.worldguard.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object TriggerLeaveRegion : Trigger("leave_region") {
    override val description = "Fires when the player leaves a WorldGuard region."

    override val categories = setOf("movement")

    override val additionalInfo = listOf("Requires WorldGuard to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location when leaving the region."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    fun dispatch(player: Player, event: RegionEvent) {
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = event.location,
                event = event
            )
        )
    }
}
