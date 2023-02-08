package com.willfp.libreforge.integrations.ecoenchants

import com.willfp.ecoenchants.type.EnchantmentTypes
import com.willfp.libreforge.integrations.ecoenchants.impl.TriggerEnchantType
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit

object EcoEnchantsIntegration {
    fun load() {
        if (Bukkit.getPluginManager().getPlugin("EcoEnchants")?.description?.version?.startsWith("9") == false) {
            return
        }

        for (type in EnchantmentTypes.values()) {
            Triggers.register(TriggerEnchantType(type.id.lowercase()))
        }
    }
}
