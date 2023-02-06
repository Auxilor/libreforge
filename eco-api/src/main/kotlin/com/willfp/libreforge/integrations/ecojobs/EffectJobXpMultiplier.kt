package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecojobs.api.event.PlayerJobExpGainEvent
import com.willfp.ecojobs.jobs.Job
import com.willfp.ecojobs.jobs.Jobs
import com.willfp.libreforge.effects.GenericMultiMultiplierEffect
import org.bukkit.event.EventHandler

class EffectJobXpMultiplier : GenericMultiMultiplierEffect<Job>(
    "job_xp_multiplier",
    Jobs::getByID,
    Jobs::values,
    "jobs"
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJobExpGainEvent) {
        val player = event.player

        val multiplier = getMultiplier(player, event.job)

        val wrapped = WrappedJobXpEvent(event)
        wrapped.amount = wrapped.amount * multiplier
    }
}
