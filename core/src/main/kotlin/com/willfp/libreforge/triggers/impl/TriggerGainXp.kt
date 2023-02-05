package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.events.NaturalExpGainEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedXpEvent
import org.bukkit.event.EventHandler

class TriggerGainXp : Trigger(
    "gain_xp", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: NaturalExpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.expChangeEvent.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                event = event.expChangeEvent,
                value = event.expChangeEvent.amount.toDouble()
            )
        )
    }
}
