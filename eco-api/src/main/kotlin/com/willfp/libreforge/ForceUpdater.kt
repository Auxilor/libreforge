package com.willfp.libreforge

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ForceUpdater(
    private val plugin: LibReforgePlugin
) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.run {
            event.player.updateEffects()
        }
    }
}
