package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.ChunkDeleteEvent
import me.angeschossen.lands.api.events.land.claiming.LandUnclaimAllEvent
import me.angeschossen.lands.api.events.land.claiming.selection.LandUnclaimSelectionEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import java.util.*

object TriggerUnclaimLand : Trigger("unclaim_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    private val multiChunkUnclaimingPlayers = mutableMapOf<UUID, Int>()

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandUnclaimSelectionEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return

        if (event.affectedChunks.size > 1) {
            multiChunkUnclaimingPlayers[player.uniqueId] = event.affectedChunks.size
        }

        Bukkit.getScheduler().runTask(plugin, Runnable {
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    event = event,
                    location = player.location,
                    value = event.affectedChunks.size.toDouble()
                )
            )
        })
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandUnclaimAllEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return

        if (event.land.chunksAmount > 1) {
            multiChunkUnclaimingPlayers[player.uniqueId] = event.land.chunksAmount
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = player.location,
                value = event.land.chunksAmount.toDouble()
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ChunkDeleteEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return
        val uuid = player.uniqueId

        val remaining = multiChunkUnclaimingPlayers[uuid]
        if (remaining != null) {
            if (remaining <= 1) {
                multiChunkUnclaimingPlayers.remove(uuid)
            } else {
                multiChunkUnclaimingPlayers[uuid] = remaining - 1
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
