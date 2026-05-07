package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.block.SignChangeEvent

object TriggerSignEdit : Trigger("sign_edit") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: SignChangeEvent) {
        val player = event.player
        val text = event.lines.firstOrNull { it.isNotBlank() } ?: ""

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                block = event.block,
                location = event.block.location,
                text = text,
                event = event
            )
        )
    }
}
