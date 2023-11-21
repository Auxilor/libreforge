package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object TriggerRunCommand : Trigger("run_command") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        plugin.scheduler.run {
            this.dispatch(
                PlayerDispatcher(player),
                TriggerData(
                    player = player,
                    location = player.location,
                    text = event.message
                )
            )
        }
    }
}
