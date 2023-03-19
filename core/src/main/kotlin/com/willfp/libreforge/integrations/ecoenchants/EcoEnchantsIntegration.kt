package com.willfp.libreforge.integrations.ecoenchants

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoenchants.type.EnchantmentTypes
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ecoenchants.impl.TriggerEnchantType
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit

object EcoEnchantsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        if (Bukkit.getPluginManager().getPlugin("EcoEnchants")?.description?.version?.startsWith("8") == true) {
            return
        }

        for (type in EnchantmentTypes.values()) {
            Triggers.register(TriggerEnchantType(type.id.lowercase()))
        }
    }

    override fun getPluginName(): String {
        return "EcoEnchants"
    }
}
