package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedDeathEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent

class TriggerDeath : Trigger(
    "death", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerDeathEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                event = WrappedDeathEvent(event)
            )
        )
    }
}
