package com.willfp.libreforge.integrations.paper.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent
import org.bukkit.event.EventHandler

object TriggerUseFlowerPot : Trigger("use_flower_pot") {
    override val description = "Fires when the player places or removes a plant from a flower pot."

    override val categories = setOf("interaction")

    override val additionalInfo = listOf("Requires Paper to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.ITEM to "The plant item placed into or removed from the pot."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerFlowerPotManipulateEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                item = event.item,
                event = event
            )
        )
    }
}
