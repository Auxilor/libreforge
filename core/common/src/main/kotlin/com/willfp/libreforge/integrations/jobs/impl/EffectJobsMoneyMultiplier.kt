package com.willfp.libreforge.integrations.jobs.impl

import com.gamingmesh.jobs.api.JobsPrePaymentEvent
import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler

object EffectJobsMoneyMultiplier : MultiplierEffect("jobs_money_multiplier") {
    override val description = "Multiplies money earned from Jobs while the holder is active."
    override val categories = setOf("economy")

    @EventHandler(ignoreCancelled = true)
    fun handle(event: JobsPrePaymentEvent) {
        val player = event.player.player ?: return

        event.amount *= getMultiplier(player.toDispatcher())
    }
}
