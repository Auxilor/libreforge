package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerBedEnterEvent

object TriggerEnterBed : Trigger("enter_bed") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerBedEnterEvent) {
        val player = event.player

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                location = player.location
            )
        )
    }
}
