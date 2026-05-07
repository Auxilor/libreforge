package com.willfp.libreforge.integrations.factionsbridge.impl.triggers

import cc.javajobs.factionsbridge.bridge.events.FactionClaimEvent
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerFactionClaim : Trigger("faction_claim") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FactionClaimEvent) {
        val player = event.getFPlayer().getPlayer() ?: return
        val chunk = event.getClaim().getChunk()
        val location = chunk.world.getBlockAt(chunk.x * 16 + 8, 64, chunk.z * 16 + 8).location
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = location
            )
        )
    }
}
