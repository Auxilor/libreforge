package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
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
        val player = event.player

        plugin.scheduler.runTask(player) {
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    location = event.to,
                    text = event.cause.name.lowercase()
                )
            )
        }
    }
}
