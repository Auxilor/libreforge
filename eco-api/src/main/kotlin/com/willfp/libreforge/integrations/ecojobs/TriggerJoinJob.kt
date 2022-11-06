package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecojobs.api.EcoJobsAPI
import com.willfp.ecojobs.api.event.PlayerJobJoinEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

class TriggerJoinJob : Trigger(
    "join_job", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJobJoinEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player as? Player ?: return

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = WrappedJobJoinEvent(event)
            ),
            value = EcoJobsAPI.instance.getJobLevel(player, event.job).toDouble()
        )
    }
}
