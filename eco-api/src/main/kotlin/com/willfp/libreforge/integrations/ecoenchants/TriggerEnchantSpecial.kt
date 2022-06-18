package com.willfp.libreforge.integrations.ecoenchants

import com.willfp.ecoenchants.enchantments.EcoEnchants
import com.willfp.ecoenchants.enchantments.meta.EnchantmentType
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent

class TriggerEnchantSpecial : Trigger(
    "enchant_special", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: EnchantItemEvent) {
        val player = event.enchanter

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        this.plugin.scheduler.runLater({
            if (EcoEnchants.hasAnyOfType(event.item, EnchantmentType.SPECIAL)) {
                this.processTrigger(
                    player,
                    TriggerData(
                        player = player,
                        location = player.location,
                        item = event.item
                    ),
                    event.expLevelCost.toDouble()
                )
            }
        }, 2)
    }
}
