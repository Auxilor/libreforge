package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.husktowns.events.UnClaimEvent
import org.bukkit.event.EventHandler

object TriggerUnclaim : Trigger("unclaim") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: UnClaimEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event
            )
        )
    }
}