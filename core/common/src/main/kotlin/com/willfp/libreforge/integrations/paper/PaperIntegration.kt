package com.willfp.libreforge.integrations.paper

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.ClassUtils
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.paper.impl.ConditionInBubble
import com.willfp.libreforge.integrations.paper.impl.ConditionInLava
import com.willfp.libreforge.integrations.paper.impl.ConditionInRain
import com.willfp.libreforge.integrations.paper.impl.EffectDropPickupItem
import com.willfp.libreforge.integrations.paper.impl.EffectElytraBoostSaveChance
import com.willfp.libreforge.integrations.paper.impl.EffectSendMinimessage
import com.willfp.libreforge.integrations.paper.impl.TriggerBeaconEffect
import com.willfp.libreforge.integrations.paper.impl.TriggerCompostItem
import com.willfp.libreforge.integrations.paper.impl.TriggerElytraBoost
import com.willfp.libreforge.integrations.paper.impl.TriggerRenameEntity
import com.willfp.libreforge.integrations.paper.impl.TriggerSwing
import com.willfp.libreforge.integrations.paper.impl.TriggerTridentAttack
import com.willfp.libreforge.integrations.paper.impl.TriggerUseFlowerPot
import com.willfp.libreforge.integrations.paper.impl.TriggerVillagerTrade
import com.willfp.libreforge.triggers.Triggers

object PaperIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        if (ClassUtils.exists("io.papermc.paper.event.player.PlayerArmSwingEvent")) {
            Triggers.register(TriggerSwing)
        }

        Effects.register(EffectSendMinimessage)
        Conditions.register(ConditionInBubble)
        Conditions.register(ConditionInLava)
        Conditions.register(ConditionInRain)
        Effects.register(EffectDropPickupItem)
        Effects.register(EffectElytraBoostSaveChance)
        Triggers.register(TriggerBeaconEffect)
        Triggers.register(TriggerCompostItem)
        Triggers.register(TriggerElytraBoost)
        Triggers.register(TriggerRenameEntity)
        Triggers.register(TriggerTridentAttack)
        Triggers.register(TriggerUseFlowerPot)
        Triggers.register(TriggerVillagerTrade)
    }

    // I know it's not a plugin but shhhh
    override fun getPluginName(): String {
        return "Paper"
    }
}
