package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerMoveEvent

object TriggerChangeWorld : Trigger("change_world") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VELOCITY,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerChangedWorldEvent) {
        val player = event.player

        this.dispatch(
            player,
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
