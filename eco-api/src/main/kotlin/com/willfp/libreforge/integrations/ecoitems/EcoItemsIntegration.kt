package com.willfp.libreforge.integrations.ecoitems

import com.willfp.libreforge.conditions.Condition

object EcoItemsIntegration {
    private lateinit var HAS_ECOITEM: Condition

    fun load() {
        HAS_ECOITEM = ConditionHasEcoItem()
    }
}
