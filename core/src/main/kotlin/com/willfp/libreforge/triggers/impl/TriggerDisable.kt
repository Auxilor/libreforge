package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.HolderDisableEvent
import com.willfp.libreforge.get
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
        if (!this.isEnabled) return

        val dispatcher = event.dispatcher

        this.dispatch(
            dispatcher,
            TriggerData(
                player = dispatcher.get(),
                victim = dispatcher.get(),
                event = event
            ),
            forceHolders = event.previousHolders
        )
    }
}
