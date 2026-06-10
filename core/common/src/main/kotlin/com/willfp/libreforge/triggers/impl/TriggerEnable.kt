package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.HolderEnableEvent
import com.willfp.libreforge.get
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerEnable : Trigger("enable") {
    override val description = "Fires when a holder is activated for the dispatcher, such as when an item is equipped."

    override val categories = setOf("meta")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: HolderEnableEvent) {
        val dispatcher = event.dispatcher

        this.dispatch(
            dispatcher,
            TriggerData(
                player = dispatcher.get(),
                victim = dispatcher.get(),
                event = event
            ),
            forceHolders = event.newHolders
        )
    }
}
