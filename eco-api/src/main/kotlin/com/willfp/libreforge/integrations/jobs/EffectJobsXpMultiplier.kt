package com.willfp.libreforge.integrations.jobs

import com.gamingmesh.jobs.Jobs
import com.gamingmesh.jobs.api.JobsExpGainEvent
import com.gamingmesh.jobs.container.Job
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.GenericMultiMultiplierEffect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

class EffectJobsXpMultiplier : GenericMultiMultiplierEffect<Job>(
    "jobs_xp_multiplier",
    Jobs::getJob,
    Jobs::getJobs,
    "jobs"
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: JobsExpGainEvent) {
        val player = event.player as? Player ?: return

        val multiplier = getMultiplier(player, event.job)

        event.exp *= multiplier
    }
}
