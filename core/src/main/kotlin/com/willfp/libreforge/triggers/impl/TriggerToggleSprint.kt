package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerToggleSprintEvent

object TriggerToggleSprint : Trigger("toggle_sprint") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerToggleSprintEvent) {
        val player = event.player

        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                velocity = player.velocity
            )
        )
    }
}
