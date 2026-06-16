package com.willfp.libreforge.integrations.factionsbridge.impl.triggers

import cc.javajobs.factionsbridge.bridge.events.FactionDisbandEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerFactionDisband : Trigger("faction_disband") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FactionDisbandEvent) {
        val faction = event.getFaction()
        val executorUuid = event.getFPlayer().getUniqueId()
        for (member in faction.getOnlineMembers()) {
            val player = member.getPlayer() ?: continue
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    text = faction.getTag(),
                    value = if (member.getUniqueId() == executorUuid) 1.0 else 0.0
                )
            )
        }
    }
}
