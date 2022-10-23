package com.willfp.libreforge.integrations.jobs

import com.gamingmesh.jobs.api.JobsPaymentEvent
import com.gamingmesh.jobs.container.CurrencyType
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.effects.GenericMultiplierEffect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

class EffectJobsMoneyMultiplier : GenericMultiplierEffect("jobs_money_multiplier") {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: JobsPaymentEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player as? Player ?: return

        val multiplier = getMultiplier(player)

        var money = event.payment[CurrencyType.MONEY] ?: return
        money *= multiplier
        event.payment[CurrencyType.MONEY] = money
    }
}
