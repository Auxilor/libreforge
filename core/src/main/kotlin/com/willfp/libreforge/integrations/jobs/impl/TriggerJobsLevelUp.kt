package com.willfp.libreforge.integrations.jobs.impl

import com.gamingmesh.jobs.api.JobsLevelUpEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerJobsLevelUp : Trigger("jobs_level_up") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: JobsLevelUpEvent) {
        val player = event.player.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                value = event.level.toDouble()
            )
        )
    }
}
