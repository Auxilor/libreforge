package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.EnchantItemEvent

class TriggerEnchantItem : Trigger(
    "enchant_item", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EnchantItemEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.enchanter
        val item = event.item

        this.dispatch(
            player,
            TriggerData(
                player = player,
                item = item,
                value = event.expLevelCost.toDouble()
            )
        )
    }
}
