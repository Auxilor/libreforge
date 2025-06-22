package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.plugin
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

    private val multiChunkClaimingPlayers = mutableMapOf<UUID, Int>()

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandClaimSelectionEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return

        if (event.affectedChunks.size > 1) {
            multiChunkClaimingPlayers[player.uniqueId] = event.affectedChunks.size
        }

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
        val uuid = player.uniqueId

        val remaining = multiChunkClaimingPlayers[uuid]
        if (remaining != null) {
            if (remaining <= 1) {
                multiChunkClaimingPlayers.remove(uuid)
            } else {
                multiChunkClaimingPlayers[uuid] = remaining - 1
            }
            return
        }

        Bukkit.getScheduler().runTask(plugin, Runnable {
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    event = event,
                    location = player.location,
                    value = 1.0
                )
            )
        })
    }
}
