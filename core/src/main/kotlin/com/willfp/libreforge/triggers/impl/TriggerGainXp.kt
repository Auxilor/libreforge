package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.events.NaturalExpGainEvent
import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerGainXp : Trigger("gain_xp") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: NaturalExpGainEvent) {
        val player = event.expChangeEvent.player

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                event = event.expChangeEvent,
                value = event.expChangeEvent.amount.toDouble()
            )
        )
    }
}
