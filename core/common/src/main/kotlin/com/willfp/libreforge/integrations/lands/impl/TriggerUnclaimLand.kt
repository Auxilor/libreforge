package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.ChunkDeleteEvent
import me.angeschossen.lands.api.events.land.claiming.LandUnclaimAllEvent
import me.angeschossen.lands.api.events.land.claiming.selection.LandUnclaimSelectionEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerUnclaimLand : Trigger("unclaim_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handleUnclaimSelection(event: LandUnclaimSelectionEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                value = event.affectedChunks.size.toDouble()
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handleUnclaimAll(event: LandUnclaimAllEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                value = event.land.maxChunks.toDouble()
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handleChunkDelete(event: ChunkDeleteEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                value = 1.0
            )
        )
    }
}