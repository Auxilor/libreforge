package com.willfp.libreforge.integrations.ecoenchants

import com.willfp.libreforge.triggers.Trigger

object EcoEnchantsIntegration {
    private lateinit var ENCHANT_SPECIAL: Trigger

    fun load() {
        ENCHANT_SPECIAL = TriggerEnchantSpecial()
    }
}
