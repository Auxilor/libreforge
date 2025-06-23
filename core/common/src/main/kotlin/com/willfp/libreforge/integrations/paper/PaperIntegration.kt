package com.willfp.libreforge.integrations.paper

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
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

        if (Prerequisite.HAS_1_18.isMet) {
            Effects.register(EffectSendMinimessage)
        }

        Triggers.register(TriggerBeaconEffect)
        Triggers.register(TriggerElytraBoost)
        Triggers.register(TriggerRenameEntity)
        Triggers.register(TriggerTridentAttack)
        Triggers.register(TriggerUseFlowerPot)
        Triggers.register(TriggerVillagerTrade)
        Effects.register(EffectElytraBoostSaveChance)
        Effects.register(EffectDropPickupItem)
        Conditions.register(ConditionInBubble)
        Conditions.register(ConditionInLava)
        Conditions.register(ConditionInRain)
    }

    // I know it's not a plugin but shhhh
    override fun getPluginName(): String {
        return "Paper"
    }
}
