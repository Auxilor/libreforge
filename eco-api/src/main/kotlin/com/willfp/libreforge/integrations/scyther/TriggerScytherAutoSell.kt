package com.willfp.libreforge.integrations.scyther

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import dev.norska.scyther.api.ScytherAutosellEvent
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack

class TriggerScytherAutoSell : Trigger(
    "scyther_auto_sell", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: ScytherAutosellEvent) {
        if (event.isCancelled) return

        this.processTrigger(
            event.player,
            TriggerData(
                player = event.player,
                block = event.block,
                item = ItemStack(event.cropMaterial, event.amount)
            )
        )
    }
}
