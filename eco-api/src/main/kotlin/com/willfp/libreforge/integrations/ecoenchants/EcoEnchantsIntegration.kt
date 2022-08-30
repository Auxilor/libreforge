package com.willfp.libreforge.integrations.ecoenchants

import com.willfp.ecoenchants.type.EnchantmentTypes

object EcoEnchantsIntegration {
    fun load() {
        for (type in EnchantmentTypes.values()) {
            TriggerEnchantType(type.id.lowercase())
        }
    }
}
