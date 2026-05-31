package com.willfp.libreforge.integrations.paper.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.entity.EntityCompostItemEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerCompostItem : Trigger("compost_item") {
    override val description = "Fires when the player composts an item in a composter."

    override val categories = setOf("interaction")

    override val additionalInfo = listOf("Requires Paper to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.ITEM to "The item that was composted.",
        TriggerParameter.ALT_VALUE to "1 if the compost level will rise, 0 if not."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.EVENT,
        TriggerParameter.ALT_VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityCompostItemEvent) {
        val player = event.entity as? Player ?: return
        val item = event.item
        val value = if (event.willRaiseLevel()) 1 else 0

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                item = item,
                event = event,
                altValue = value.toDouble()
            )
        )
    }
}