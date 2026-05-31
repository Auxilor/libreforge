package com.willfp.libreforge.integrations.jobs.impl

import com.gamingmesh.jobs.Jobs
import com.gamingmesh.jobs.api.JobsExpGainEvent
import com.gamingmesh.jobs.container.Job
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object EffectJobsXpMultiplier : MultiMultiplierEffect<Job>("jobs_xp_multiplier") {
    override val description = "Multiplies XP earned from Jobs for one or all jobs while the holder is active."
    override val categories = setOf("economy")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The XP multiplier. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "jobs",
            description = "List of job names to apply the multiplier to. If omitted, applies to all jobs.",
            type = ArgType.STRING_LIST
        )
    }

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

        event.exp *= getMultiplier(player.toDispatcher(), event.job)
    }
}
