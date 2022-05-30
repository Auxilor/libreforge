package com.willfp.libreforge.integrations.reforges

import com.willfp.libreforge.conditions.Condition

object ReforgesIntegration {
    private lateinit var HAS_REFORGE: Condition

    fun load() {
        HAS_REFORGE = ConditionHasReforge()
    }
}
