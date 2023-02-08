package com.willfp.libreforge.integrations.ecojobs.impl

import com.willfp.ecojobs.api.event.PlayerJobExpGainEvent
import com.willfp.ecojobs.jobs.Job
import com.willfp.ecojobs.jobs.Jobs
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import org.bukkit.event.EventHandler

object EffectJobXpMultiplier : MultiMultiplierEffect<Job>("job_xp_multiplier") {
    override val key = "jobs"

    override fun getElement(key: String): Job? {
        return Jobs.getByID(key)
    }

    override fun getAllElements(): Collection<Job> {
        return Jobs.values()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJobExpGainEvent) {
        event.amount *= getMultiplier(event.player, event.job)
    }
}
