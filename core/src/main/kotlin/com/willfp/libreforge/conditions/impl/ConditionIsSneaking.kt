package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerToggleSneakEvent

class ConditionIsSneaking : Condition("is_sneaking") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: PlayerToggleSneakEvent) {
        val player = event.player

        player.updateEffects(noRescan = true)
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.isSneaking == (config.getBoolOrNull("is_sneaking") ?: true)
    }
}