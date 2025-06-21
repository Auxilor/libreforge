package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.land.claiming.LandUnclaimAllEvent
import me.angeschossen.lands.api.events.land.claiming.selection.LandUnclaimSelectionEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerUnclaimLand : Trigger("unclaim_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandUnclaimSelectionEvent) {
        val player = event.landPlayer as Player
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandUnclaimAllEvent) {
        val player = event.landPlayer as Player
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
            )
        )
    }
}