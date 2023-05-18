package com.willfp.libreforge.integrations.worldguard.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerLeaveRegion : Trigger("leave_region") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler
    fun dispatch(player: Player, event: RegionEvent) {
        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = event.location,
                event = event
            )
        )
    }
}
