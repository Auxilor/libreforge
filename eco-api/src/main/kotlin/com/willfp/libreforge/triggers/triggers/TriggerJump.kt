package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.events.PlayerJumpEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.EventHandler

class TriggerJump : Trigger("jump") {
    @EventHandler(ignoreCancelled = true)
    fun onJump(event: PlayerJumpEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }
        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location
            )
        )
    }
}
