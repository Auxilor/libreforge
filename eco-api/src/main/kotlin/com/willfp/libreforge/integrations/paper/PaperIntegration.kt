package com.willfp.libreforge.integrations.paper

import com.willfp.eco.util.ClassUtils
import com.willfp.libreforge.triggers.Trigger

object PaperIntegration {
    private lateinit var SWING: Trigger

    fun load() {
        if (ClassUtils.exists("io.papermc.paper.event.player.PlayerArmSwingEvent")) {
            SWING = TriggerSwing()
        }
    }
}