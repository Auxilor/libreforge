package com.willfp.libreforge.integrations.paper

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.triggers.Trigger

object PaperIntegration {
    private lateinit var SWING: Trigger

    fun load() {
        if (Prerequisite.HAS_1_17.isMet) {
            SWING = TriggerSwing()
        }
    }
}