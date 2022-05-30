package com.willfp.libreforge.integrations.talismans

import com.willfp.libreforge.conditions.Condition

object TalismansIntegration {
    private lateinit var HAS_TALISMAN: Condition

    fun load() {
        HAS_TALISMAN = ConditionHasTalisman()
    }
}
