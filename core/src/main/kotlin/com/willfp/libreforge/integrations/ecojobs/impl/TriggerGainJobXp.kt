package com.willfp.libreforge.integrations.ecojobs.impl

import com.willfp.ecojobs.api.event.PlayerJobExpGainEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerGainJobXp : Trigger("gain_job_xp") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJobExpGainEvent) {
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                value = event.amount
            )
        )
    }
}
