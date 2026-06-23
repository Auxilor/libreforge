package com.willfp.libreforge.integrations.huskintegration.huskclaims.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.huskclaims.event.BukkitTrustEvent
import org.bukkit.event.EventHandler

object TriggerTrustPlayer : Trigger("trust_player") {
    override val description = "Fires when the player grants trust in a HuskClaims claim."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires HuskClaims to be installed.")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The trust level name granted."
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BukkitTrustEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                text = event.trustLevel.name
            )
        )
    }
}
