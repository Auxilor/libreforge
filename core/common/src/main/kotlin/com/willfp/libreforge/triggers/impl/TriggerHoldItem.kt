package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemHeldEvent

object TriggerHoldItem : Trigger("hold_item") {
    override val description = "Fires when the player switches to a different hotbar slot."

    override val categories = setOf("inventory")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.ITEM to "The item in the newly selected hotbar slot."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerItemHeldEvent) {
        val player = event.player
        if (event.isCancelled) {
            return
        }

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                item = event.player.inventory.getItem(event.newSlot)
            )
        )
    }
}
