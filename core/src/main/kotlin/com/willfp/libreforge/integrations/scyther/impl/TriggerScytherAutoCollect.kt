package com.willfp.libreforge.integrations.scyther.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import dev.norska.scyther.api.ScytherAutocollectEvent
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack

object TriggerScytherAutoCollect : Trigger("scyther_auto_collect") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ScytherAutocollectEvent) {
        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                block = event.block,
                item = ItemStack(event.cropMaterial, event.dropAmount)
            )
        )
    }
}
