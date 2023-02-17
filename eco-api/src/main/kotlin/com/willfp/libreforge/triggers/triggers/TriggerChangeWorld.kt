package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerMoveEvent

class TriggerChangeWorld : Trigger(
    "change_world", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VELOCITY,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerChangedWorldEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = event.player.location,
                velocity = player.velocity,
                item = player.inventory.itemInMainHand
            )
        )
    }
}
