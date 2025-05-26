package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.husktowns.events.PlayerLeaveTownEvent
import org.bukkit.event.EventHandler

object TriggerLeaveClaimedLand : Trigger("leave_claimed_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    // This is the same as TriggerEnterTownLand, but with the event changed to PlayerLeaveTownEvent
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