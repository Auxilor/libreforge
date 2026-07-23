package com.willfp.libreforge.integrations.huskintegration.huskclaims.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.huskclaims.event.BukkitUnTrustEvent
import org.bukkit.event.EventHandler

object TriggerUntrustPlayer : Trigger("untrust_player") {
    override val description = "Fires when the player removes trust in a HuskClaims claim."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires HuskClaims to be installed.")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The trust level name removed."
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BukkitUnTrustEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                text = event.trustLevel.id
            )
        )
    }
}
