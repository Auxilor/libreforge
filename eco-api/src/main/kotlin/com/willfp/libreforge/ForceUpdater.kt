package com.willfp.libreforge

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ForceUpdater(
    private val plugin: LibReforgePlugin
) : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(event: PlayerJoinEvent) {
        event.player.purgePreviousStates()

        plugin.scheduler.run {
            event.player.updateEffects()
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        for (holder in player.getHolders()) {
            for ((effect) in holder.effects) {
                effect.disableForPlayer(player)
            }
        }
    }
}
