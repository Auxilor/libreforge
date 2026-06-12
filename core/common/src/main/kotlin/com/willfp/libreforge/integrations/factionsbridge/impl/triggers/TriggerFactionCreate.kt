package com.willfp.libreforge.integrations.factionsbridge.impl.triggers

import cc.javajobs.factionsbridge.bridge.events.FactionCreateEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerFactionCreate : Trigger("faction_create") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FactionCreateEvent) {
        val player = event.getPlayer() ?: return
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                text = event.getTag()
            )
        )
    }
}
