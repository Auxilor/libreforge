package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent

class TriggerCatchFish : Trigger(
    "catch_fish", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerFishEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) {
            return
        }

        PlayerFishEvent.State.FAILED_ATTEMPT

        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = event.hook.location
            )
        )
    }
}
