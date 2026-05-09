package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.Biome
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

object TriggerChangeBiome : Trigger("change_biome") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    private val lastBiome = mutableMapOf<UUID, Biome>()

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        if (Prerequisite.HAS_PAPER.isMet) {
            if (!event.hasChangedBlock()) {
                return
            }
        }

        val newBiome = event.to.block.biome
        val previous = lastBiome[player.uniqueId]

        if (previous == newBiome) {
            return
        }

        lastBiome[player.uniqueId] = newBiome

        // Don't fire on first seen biome, only on transitions
        if (previous == null) {
            return
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                text = newBiome.key.toString()
            )
        )
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        lastBiome.remove(event.player.uniqueId)
    }
}
