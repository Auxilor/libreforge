package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.events.PlayerJumpEvent
import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerJump : Trigger("jump") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT,
        TriggerParameter.VELOCITY
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJumpEvent) {
        val player = event.player

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                velocity = player.velocity
            )
        )
    }
}
