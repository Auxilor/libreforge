package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerLevelChangeEvent

class TriggerLevelUpXp : Trigger(
    "level_up_xp", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerLevelChangeEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        if (event.newLevel < event.oldLevel) {
            return
        }

        this.processTrigger(
            player,
            TriggerData(
                player = player
            ),
            event.newLevel.toDouble()
        )
    }
}
