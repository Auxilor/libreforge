package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent

object TriggerDeath : Trigger("death") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerDeathEvent) {
        val player = event.entity

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = event
            )
        )
    }
}
