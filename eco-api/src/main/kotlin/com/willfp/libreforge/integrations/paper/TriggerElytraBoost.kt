package com.willfp.libreforge.integrations.paper

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerElytraBoost : Trigger(
    "elytra_boost", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerElytraBoostEvent) {
        val player = event.player

        if (event.isCancelled) {
            return
        }

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location
            )
        )
    }
}
