package com.willfp.libreforge.integrations.factionsbridge.impl.triggers

import cc.javajobs.factionsbridge.bridge.events.FactionJoinEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerFactionJoin : Trigger("faction_join") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FactionJoinEvent) {
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
