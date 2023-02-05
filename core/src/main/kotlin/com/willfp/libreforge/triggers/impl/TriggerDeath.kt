package com.willfp.libreforge.triggers.impl

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
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerDeathEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.entity

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = event
            )
        )
    }
}
