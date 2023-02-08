package com.willfp.libreforge.integrations.paper

import com.willfp.eco.util.ClassUtils
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.paper.impl.EffectElytraBoostSaveChance
import com.willfp.libreforge.integrations.paper.impl.TriggerBeaconEffect
import com.willfp.libreforge.integrations.paper.impl.TriggerElytraBoost
import com.willfp.libreforge.integrations.paper.impl.TriggerSwing
import com.willfp.libreforge.integrations.paper.impl.TriggerVillagerTrade
import com.willfp.libreforge.triggers.Triggers

object PaperIntegration {
    fun load() {
        if (ClassUtils.exists("io.papermc.paper.event.player.PlayerArmSwingEvent")) {
            Triggers.register(TriggerSwing)
        }

        Triggers.register(TriggerBeaconEffect)
        Triggers.register(TriggerElytraBoost)
        Triggers.register(TriggerVillagerTrade)
        Effects.register(EffectElytraBoostSaveChance)
    }
}
