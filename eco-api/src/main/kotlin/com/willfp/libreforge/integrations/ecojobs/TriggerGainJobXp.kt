package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecojobs.api.event.PlayerJobExpGainEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerGainJobXp : Trigger(
    "gain_job_xp", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJobExpGainEvent) {
        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = WrappedJobXpEvent(event)
            ),
            event.amount
        )
    }
}
