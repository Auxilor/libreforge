package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.ChunkPostClaimEvent
import me.angeschossen.lands.api.events.land.claiming.selection.LandClaimSelectionEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import java.util.*

object TriggerClaimLand : Trigger("claim_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    private val multiChunkClaimingPlayers = mutableSetOf<UUID>()

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandClaimSelectionEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return

        // Mark this player as doing a multi-chunk claim if more than 1 chunk
        if (event.affectedChunks.size > 1) {
            multiChunkClaimingPlayers.add(player.uniqueId)
        }

        // Dispatch the total claim event
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = player.location,
                value = event.affectedChunks.size.toDouble()
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ChunkPostClaimEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return

        if (multiChunkClaimingPlayers.remove(player.uniqueId)) {
            return
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = player.location,
                value = 1.0
            )
        )
    }
}
