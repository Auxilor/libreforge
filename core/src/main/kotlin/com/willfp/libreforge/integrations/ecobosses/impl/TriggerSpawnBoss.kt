package com.willfp.libreforge.integrations.ecobosses.impl

import com.willfp.ecobosses.events.BossSpawnEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerSpawnBoss : Trigger("spawn_boss") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BossSpawnEvent) {
        val player = event.spawner ?: return
        val location = event.location

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = location
            )
        )
    }
}
