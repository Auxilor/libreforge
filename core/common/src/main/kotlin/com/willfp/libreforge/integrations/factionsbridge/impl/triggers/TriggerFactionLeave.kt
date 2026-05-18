package com.willfp.libreforge.integrations.factionsbridge.impl.triggers

import cc.javajobs.factionsbridge.bridge.events.FactionLeaveEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerFactionLeave : Trigger("faction_leave") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FactionLeaveEvent) {
        val player = event.getFPlayer().getPlayer() ?: return
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                text = event.getFaction().getTag()
            )
        )
    }
}
