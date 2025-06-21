package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.ChunkPostClaimEvent
import me.angeschossen.lands.api.events.land.claiming.selection.LandClaimSelectionEvent
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler

object TriggerClaimLand : Trigger("claim_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: Event) {
        val result = when (event) {
            is LandClaimSelectionEvent -> event.landPlayer?.let { it to event.affectedChunks.size }
            is ChunkPostClaimEvent -> event.landPlayer?.let { it to 1}
            else -> null
        } ?: return

        val (landPlayer, chunkCount) = result
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return
        val location = player.location

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                value = chunkCount.toDouble()
            )
        )
    }
}