package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
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
        if (!isEnabled) return
        val player = event.player

        plugin.scheduler.run {
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    location = player.location,
                    text = event.message
                )
            )
        }
    }
}
