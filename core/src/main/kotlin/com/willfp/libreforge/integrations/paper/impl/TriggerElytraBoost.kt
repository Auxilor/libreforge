package com.willfp.libreforge.integrations.paper.impl

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerElytraBoost : Trigger("elytra_boost") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerElytraBoostEvent) {
        val player = event.player

        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                location = player.location
            )
        )
    }
}
