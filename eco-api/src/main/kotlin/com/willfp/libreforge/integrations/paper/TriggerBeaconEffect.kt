package com.willfp.libreforge.integrations.paper

import com.destroystokyo.paper.event.block.BeaconEffectEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedBeaconEffectEvent
import org.bukkit.event.EventHandler

class TriggerBeaconEffect : Trigger(
    "beacon_effect", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BeaconEffectEvent) {
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
                location = player.location,
                event = WrappedBeaconEffectEvent(event)
            )
        )
    }
}
