package com.willfp.libreforge.integrations.paper

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.ClassUtils
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.paper.impl.EffectDropPickupItem
import com.willfp.libreforge.integrations.paper.impl.EffectElytraBoostSaveChance
import com.willfp.libreforge.integrations.paper.impl.TriggerBeaconEffect
import com.willfp.libreforge.integrations.paper.impl.TriggerElytraBoost
import com.willfp.libreforge.integrations.paper.impl.TriggerSwing
import com.willfp.libreforge.integrations.paper.impl.TriggerVillagerTrade
import com.willfp.libreforge.triggers.Triggers

object PaperIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        if (ClassUtils.exists("io.papermc.paper.event.player.PlayerArmSwingEvent")) {
            Triggers.register(TriggerSwing)
        }

        Triggers.register(TriggerBeaconEffect)
        Triggers.register(TriggerElytraBoost)
        Triggers.register(TriggerVillagerTrade)
        Effects.register(EffectElytraBoostSaveChance)
        Effects.register(EffectDropPickupItem)
    }

    // I know it's not a plugin but shhhh
    override fun getPluginName(): String {
        return "Paper"
    }
}
