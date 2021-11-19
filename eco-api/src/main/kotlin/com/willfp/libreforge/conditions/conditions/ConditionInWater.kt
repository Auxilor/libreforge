package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerMoveEvent

class ConditionInWater: Condition("in_water") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerMoveEvent) {
        val player = event.player

        if (event.from.world?.getBlockAt(event.from)?.type == event.to.world?.getBlockAt(event.to)?.type) {
            return
        }

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: JSONConfig): Boolean {
        return player.isInWater
    }
}