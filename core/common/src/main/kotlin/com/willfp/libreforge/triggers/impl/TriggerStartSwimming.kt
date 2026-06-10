package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityToggleSwimEvent

object TriggerStartSwimming : Trigger("start_swimming") {
    override val description = "Fires when a player starts swimming."

    override val categories = setOf("movement")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location when they started swimming."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityToggleSwimEvent) {
        if (!event.isSwimming) return
        val player = event.entity as? Player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location
            )
        )
    }
}
