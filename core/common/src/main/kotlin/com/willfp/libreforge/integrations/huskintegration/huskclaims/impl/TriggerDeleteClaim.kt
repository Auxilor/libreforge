package com.willfp.libreforge.integrations.huskintegration.huskclaims.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.huskclaims.event.BukkitDeleteClaimEvent
import org.bukkit.event.EventHandler

object TriggerDeleteClaim : Trigger("delete_claim") {
    override val description = "Fires when the player deletes a HuskClaims claim."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires HuskClaims to be installed.")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BukkitDeleteClaimEvent) {
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
