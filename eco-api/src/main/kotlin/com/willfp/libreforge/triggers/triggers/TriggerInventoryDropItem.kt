package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDropItemEvent

class TriggerInventoryDropItem : Trigger(
    "inventory_drop_item", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDropItemEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.entity as? Player ?: return

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                item = event.itemDrop.itemStack
            ),
            value = event.itemDrop.itemStack.amount.toDouble()
        )
    }
}
