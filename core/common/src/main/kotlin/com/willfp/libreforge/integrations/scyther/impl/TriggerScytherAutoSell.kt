package com.willfp.libreforge.integrations.scyther.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import dev.norska.scyther.api.ScytherAutosellEvent
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack

object TriggerScytherAutoSell : Trigger("scyther_auto_sell") {
    override val description = "Fires when Scyther auto-sells a crop for the player."

    override val categories = setOf("economy")

    override val additionalInfo = listOf("Requires Scyther to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The crop block that was auto-sold.",
        TriggerParameter.ITEM to "The sold crop item."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: ScytherAutosellEvent) {
        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                player = event.player,
                block = event.block,
                item = ItemStack(event.cropMaterial, event.amount)
            )
        )
    }
}
