package com.willfp.libreforge.integrations.scyther

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import dev.norska.scyther.api.ScytherAutocollectEvent
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack

class TriggerScytherAutoCollect : Trigger(
    "scyther_auto_collect", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: ScytherAutocollectEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        if (event.isCancelled) return

        this.processTrigger(
            event.player,
            TriggerData(
                player = event.player,
                block = event.block,
                item = ItemStack(event.cropMaterial, event.dropAmount)
            )
        )
    }
}