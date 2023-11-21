package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerRespawnEvent

object TriggerRespawn : Trigger("respawn") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerRespawnEvent) {
        this.dispatch(
            EntityDispatcher(event.player),
            TriggerData(
                player = event.player,
                location = event.player.location
            )
        )
    }
}
