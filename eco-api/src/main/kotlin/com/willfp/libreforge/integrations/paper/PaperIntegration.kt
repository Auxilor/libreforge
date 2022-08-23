package com.willfp.libreforge.integrations.paper

import com.willfp.eco.util.ClassUtils
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.Trigger

object PaperIntegration {
    private lateinit var SWING: Trigger
    private lateinit var BEACON_EFFECT: Trigger
    private lateinit var ELYTRA_BOOST: Trigger
    private lateinit var ELYTRA_BOOST_SAVE_CHANCE: Effect

    fun load() {
        if (ClassUtils.exists("io.papermc.paper.event.player.PlayerArmSwingEvent")) {
            SWING = TriggerSwing()
        }

        ELYTRA_BOOST = TriggerElytraBoost()
        BEACON_EFFECT = TriggerBeaconEffect()
        ELYTRA_BOOST_SAVE_CHANCE = EffectElytraBoostSaveChance()
    }
}