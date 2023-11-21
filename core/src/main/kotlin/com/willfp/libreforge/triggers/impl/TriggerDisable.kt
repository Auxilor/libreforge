package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.HolderDisableEvent
import com.willfp.libreforge.EntityDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerDisable : Trigger("disable") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: HolderDisableEvent) {
        val player = event.player

        this.dispatch(
            EntityDispatcher(player),
            TriggerData(
                player = player,
                event = event
            ),
            forceHolders = event.previousHolders
        )
    }
}
