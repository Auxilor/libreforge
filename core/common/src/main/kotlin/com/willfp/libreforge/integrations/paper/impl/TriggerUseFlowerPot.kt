package com.willfp.libreforge.integrations.paper.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent
import org.bukkit.event.EventHandler

object TriggerUseFlowerPot : Trigger("use_flower_pot") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.EVENT
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
