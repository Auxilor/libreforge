package com.willfp.libreforge.integrations.jobs.impl

import com.gamingmesh.jobs.api.JobsPaymentEvent
import com.gamingmesh.jobs.container.CurrencyType
import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object EffectJobsMoneyMultiplier : MultiplierEffect("jobs_money_multiplier") {
    override val description = "Multiplies money earned from Jobs while the holder is active."
    override val categories = setOf("economy")

    @EventHandler(ignoreCancelled = true)
    fun handle(event: JobsPaymentEvent) {
        val player = event.player as? Player ?: return

        val multiplier = getMultiplier(player.toDispatcher())

        var money = event.payment[CurrencyType.MONEY] ?: return
        money *= multiplier
        event.payment[CurrencyType.MONEY] = money
    }
}
