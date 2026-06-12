package com.willfp.libreforge.integrations.factionsbridge.impl.triggers

import cc.javajobs.factionsbridge.bridge.events.FactionRenameEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerFactionRename : Trigger("faction_rename") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FactionRenameEvent) {
        val faction = event.getFaction()
        val leaderUuid = faction.getLeader()?.getUniqueId()
        for (member in faction.getOnlineMembers()) {
            val player = member.getPlayer() ?: continue
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    text = event.getName(),
                    value = if (leaderUuid != null && member.getUniqueId() == leaderUuid) 1.0 else 0.0
                )
            )
        }
    }
}
