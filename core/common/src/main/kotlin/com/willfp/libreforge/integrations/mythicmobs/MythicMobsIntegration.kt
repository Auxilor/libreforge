package com.willfp.libreforge.integrations.mythicmobs

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.mythicmobs.impl.effect.EffectCastMythicSkill
import com.willfp.libreforge.integrations.mythicmobs.impl.listener.MythicMobsTelekinesisListener
import com.willfp.libreforge.integrations.mythicmobs.impl.trigger.TriggerTakeMythicDamage
import com.willfp.libreforge.triggers.Triggers

object MythicMobsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectCastMythicSkill)
        plugin.eventManager.registerListener(MythicMobsTelekinesisListener)
        Triggers.register(TriggerTakeMythicDamage)
    }

    override fun getPluginName(): String {
        return "MythicMobs"
    }
}
