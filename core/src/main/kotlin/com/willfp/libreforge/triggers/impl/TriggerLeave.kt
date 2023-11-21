package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

object TriggerLeave : Trigger("leave") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerQuitEvent) {
        this.dispatch(
            PlayerDispatcher(event.player),
            TriggerData(
                player = event.player,
                location = event.player.location
            )
        )
    }
}
