package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

class TriggerChangeWorld : Trigger(
    "change_world", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VELOCITY,
        TriggerParameter.EVENT,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerMoveEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        if (event.to.world != event.from.world) {
            return
        }

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = event.to,
                velocity = player.velocity,
                event = GenericCancellableEvent(event),
                item = player.inventory.itemInMainHand
            )
        )
    }
}
