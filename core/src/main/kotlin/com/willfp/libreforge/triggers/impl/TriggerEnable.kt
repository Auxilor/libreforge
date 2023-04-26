package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.effects.events.EffectEnableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerEnable : Trigger("enable") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EffectEnableEvent) {
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                event = event
            )
        )
    }
}
