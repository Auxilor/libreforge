package com.willfp.libreforge.integrations.mythicmobs

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.mythicmobs.impl.EffectCastMythicSkill

object MythicMobsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectCastMythicSkill)
    }

    override fun getPluginName(): String {
        return "mcMMO"
    }
}
