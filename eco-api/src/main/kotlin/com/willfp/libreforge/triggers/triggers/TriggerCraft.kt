package com.willfp.libreforge.triggers.triggers

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent

class TriggerCraft : Trigger(
    "craft", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: CraftItemEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.whoClicked as? Player ?: return
        val item = event.recipe.result

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                item = item
            )
        )
    }
}
