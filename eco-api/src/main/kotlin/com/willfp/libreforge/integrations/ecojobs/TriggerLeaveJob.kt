package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecojobs.api.event.PlayerJobLeaveEvent
import com.willfp.ecojobs.api.getJobLevel
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

class TriggerLeaveJob : Trigger(
    "leave_job", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJobLeaveEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player as? Player ?: return

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location
            ),
            value = player.getJobLevel(event.job).toDouble()
        )
    }
}
