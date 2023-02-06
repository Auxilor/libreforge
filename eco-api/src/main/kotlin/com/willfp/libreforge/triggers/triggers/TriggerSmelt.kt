package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.Furnace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.FurnaceSmeltEvent

class TriggerSmelt : Trigger(
    "smelt", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: FurnaceSmeltEvent) {
        val furnace = event.block.state as? Furnace ?: return
        val player = furnace.inventory.viewers.firstOrNull() as? Player ?: return
        val item = event.result

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                item = item
            )
        )
    }
}
