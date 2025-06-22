package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.ChunkPostClaimEvent
import me.angeschossen.lands.api.events.land.claiming.selection.LandClaimSelectionEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerClaimLand : Trigger("claim_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handleClaimSelection(event: LandClaimSelectionEvent) {
        if (event.affectedChunks.size > 1.0) {
            val landPlayer = event.landPlayer ?: return
            val player = Bukkit.getPlayer(landPlayer.uid) ?: return

            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    event = event,
                    location = player.location,
                    value = event.affectedChunks.size.toDouble()
                )
            )
            return
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handlePostClaim(event: ChunkPostClaimEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return

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