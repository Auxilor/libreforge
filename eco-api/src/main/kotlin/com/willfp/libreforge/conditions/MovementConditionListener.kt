package com.willfp.libreforge.conditions

import com.willfp.libreforge.updateEffects
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class MovementConditionListener : Listener {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerMoveEvent) {
        if (event.from.block == event.to.block) {
            return
        }
        event.player.updateEffects(noRescan = true)
    }
}