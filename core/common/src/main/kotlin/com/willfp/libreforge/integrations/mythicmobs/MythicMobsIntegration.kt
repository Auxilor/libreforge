package com.willfp.libreforge.integrations.mythicmobs

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.mythicmobs.impl.EffectCastMythicSkill
import com.willfp.libreforge.integrations.mythicmobs.impl.EffectMythicTelekinesis
import com.willfp.libreforge.integrations.mythicmobs.impl.TriggerTakeMythicDamage
import com.willfp.libreforge.triggers.Triggers

object MythicMobsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectCastMythicSkill)
        Effects.register(EffectMythicTelekinesis)
        Triggers.register(TriggerTakeMythicDamage)
    }

    override fun getPluginName(): String {
        return "MythicMobs"
    }
}
