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
        val player = event.expChangeEvent.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                event = WrappedXpEvent(event.expChangeEvent)
            ),
            event.expChangeEvent.amount.toDouble()
        )
    }
}
