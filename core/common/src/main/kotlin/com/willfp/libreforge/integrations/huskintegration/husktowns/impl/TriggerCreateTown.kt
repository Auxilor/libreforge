package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.husktowns.events.TownCreateEvent
import org.bukkit.event.EventHandler

object TriggerCreateTown : Trigger("create_town") {
    override val description = "Fires when the player creates a HuskTowns town."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires HuskTowns to be installed.")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: TownCreateEvent) {
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