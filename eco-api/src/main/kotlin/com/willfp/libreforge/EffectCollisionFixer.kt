package com.willfp.libreforge

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object EffectCollisionFixer : Listener {
    @EventHandler
    fun clearOnQuit(event: PlayerQuitEvent) {
        val player = event.player
        for (effect in player.getActiveEffects()) {
            effect.disableFor(player)
        }
    }
}
