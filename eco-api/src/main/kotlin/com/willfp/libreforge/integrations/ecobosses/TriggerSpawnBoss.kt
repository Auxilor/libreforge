package com.willfp.libreforge.integrations.ecobosses

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecobosses.events.BossSpawnEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerSpawnBoss : Trigger(
    "spawn_boss", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BossSpawnEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.spawner ?: return
        val location = event.location

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = location
            )
        )
    }
}
