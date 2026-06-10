package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.EnchantItemEvent

object TriggerEnchantItem : Trigger("enchant_item") {
    override val description = "Fires when the player enchants an item at an enchanting table."

    override val categories = setOf("inventory")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.ITEM to "The item that was enchanted.",
        TriggerParameter.VALUE to "The XP level cost of the enchantment."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EnchantItemEvent) {
        val player = event.enchanter
        val item = event.item

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = item,
                event = event,
                value = event.expLevelCost.toDouble()
            )
        )
    }
}
