package com.willfp.libreforge.integrations.ecojobs.impl

import com.willfp.ecojobs.api.EcoJobsAPI
import com.willfp.ecojobs.api.event.PlayerJobLeaveEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerLeaveJob : Trigger("leave_job") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJobLeaveEvent) {
        val player = event.player as? Player ?: return

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                value = EcoJobsAPI.instance.getJobLevel(player, event.job).toDouble()
            )
        )
    }
}
