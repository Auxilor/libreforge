package com.willfp.libreforge.integrations.ecoenchants

import com.willfp.ecoenchants.type.EnchantmentTypes
import org.bukkit.Bukkit

object EcoEnchantsIntegration {
    fun load() {
        if (Bukkit.getPluginManager().getPlugin("EcoEnchants")?.description?.version?.startsWith("9") == true) {
            return
        }

        for (type in EnchantmentTypes.values()) {
            TriggerEnchantType(type.id.lowercase())
        }
    }
}
