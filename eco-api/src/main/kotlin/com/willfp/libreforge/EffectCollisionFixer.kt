package com.willfp.libreforge

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class EffectCollisionFixer(
    private val plugin: LibReforgePlugin
) : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun clearOnQuit(event: PlayerQuitEvent) {
        val player = event.player
        for (effect in player.getActiveEffects()) {
            effect.disableFor(player)
        }
    }

    @EventHandler
    fun scanOnJoin(event: PlayerJoinEvent) {
        val player = event.player

        plugin.scheduler.run {
            player.updateEffects()
        }
    }
}
