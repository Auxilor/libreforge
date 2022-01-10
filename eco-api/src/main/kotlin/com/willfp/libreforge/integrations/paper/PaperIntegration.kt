package com.willfp.libreforge.integrations.paper

import com.willfp.libreforge.triggers.Trigger

object PaperIntegration {
    private lateinit var SWING: Trigger

    fun load() {
        SWING = TriggerSwing()
    }
}