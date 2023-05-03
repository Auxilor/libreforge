package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerTeleportEvent

object TriggerTeleport : Trigger("teleport") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerTeleportEvent) {
        this.dispatch(
            event.player,
            TriggerData(
                player = event.player,
                location = event.to.toBlockLocation(),
                text = event.cause.name
            )
        )
    }
}