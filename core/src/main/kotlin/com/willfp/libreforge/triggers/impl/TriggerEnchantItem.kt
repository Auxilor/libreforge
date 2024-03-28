package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.EnchantItemEvent

object TriggerEnchantItem : Trigger("enchant_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EnchantItemEvent) {
        if (!isEnabled) return
        val player = event.enchanter
        val item = event.item

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = item,
                value = event.expLevelCost.toDouble()
            )
        )
    }
}
