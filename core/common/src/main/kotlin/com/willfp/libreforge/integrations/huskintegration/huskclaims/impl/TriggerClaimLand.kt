package com.willfp.libreforge.integrations.huskintegration.huskclaims.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.huskclaims.event.BukkitCreateClaimEvent
import org.bukkit.event.EventHandler

object TriggerClaimLand : Trigger("claim_land") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BukkitCreateClaimEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event
            )
        )
    }
}