package com.willfp.libreforge.integrations.scyther.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import dev.norska.scyther.api.ScytherAutosellEvent
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack

object TriggerScytherAutoSell : Trigger("scyther_auto_sell") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ScytherAutosellEvent) {
        this.dispatch(
            event.player,
            TriggerData(
                player = event.player,
                block = event.block,
                item = ItemStack(event.cropMaterial, event.amount)
            )
        )
    }
}
