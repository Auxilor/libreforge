package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent

class TriggerPickUpItem : Trigger(
    "pick_up_item", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                item = event.item.itemStack
            ),
            value = event.item.itemStack.amount.toDouble()
        )
    }
}
