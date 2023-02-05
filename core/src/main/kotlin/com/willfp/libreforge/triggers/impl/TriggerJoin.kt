package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

class TriggerJoin : Trigger(
    "join", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJoinEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        this.dispatch(
            event.player,
            TriggerData(
                player = event.player,
                location = event.player.location
            )
        )
    }
}
