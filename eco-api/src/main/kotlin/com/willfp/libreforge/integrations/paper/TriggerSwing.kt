package com.willfp.libreforge.integrations.paper

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.player.PlayerArmSwingEvent
import org.bukkit.event.EventHandler

class TriggerSwing : Trigger(
    "swing", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerArmSwingEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

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
