package com.willfp.libreforge.integrations.jobs

import com.gamingmesh.jobs.api.JobsLevelUpEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerJobsLevelUp : Trigger(
    "jobs_level_up", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: JobsLevelUpEvent) {
        val player = event.player.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location
            ),
            event.level.toDouble()
        )
    }
}
