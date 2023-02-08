package com.willfp.libreforge.integrations.paper.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.player.PlayerArmSwingEvent
import org.bukkit.event.EventHandler

object TriggerSwing : Trigger("swing") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerArmSwingEvent) {
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location
            )
        )
    }
}
