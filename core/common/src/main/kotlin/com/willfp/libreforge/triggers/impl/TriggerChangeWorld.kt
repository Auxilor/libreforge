package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent

object TriggerChangeWorld : Trigger("change_world") {
    override val description = "Fires when the player moves to a different world."

    override val categories = setOf("movement", "world")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location in the destination world.",
        TriggerParameter.VELOCITY to "The player's velocity at the time of the world change.",
        TriggerParameter.ITEM to "The item in the player's main hand.",
        TriggerParameter.TEXT to "The name of the world the player moved to."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VELOCITY,
        TriggerParameter.ITEM,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerChangedWorldEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = event.player.location,
                velocity = player.velocity,
                event = event,
                item = player.inventory.itemInMainHand,
                text = event.player.world.name
            )
        )
    }
}
