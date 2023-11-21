package com.willfp.libreforge.integrations.jobs.impl

import com.gamingmesh.jobs.Jobs
import com.gamingmesh.jobs.api.JobsExpGainEvent
import com.gamingmesh.jobs.container.Job
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.EntityDispatcher
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object EffectJobsXpMultiplier : MultiMultiplierEffect<Job>("jobs_xp_multiplier") {
    override val key = "jobs"

    override fun getElement(key: String): Job? {
        return Jobs.getJob(key)
    }

    override fun getAllElements(): Collection<Job> {
        return Jobs.getJobs()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: JobsExpGainEvent) {
        val player = event.player as? Player ?: return

        event.exp *= getMultiplier(EntityDispatcher(player), event.job)
    }
}
