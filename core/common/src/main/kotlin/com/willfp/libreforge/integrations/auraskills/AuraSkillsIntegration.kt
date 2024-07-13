package com.willfp.libreforge.integrations.auraskills

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.arguments.EffectArguments
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.auraskills.impl.ArgumentManaCost
import com.willfp.libreforge.integrations.auraskills.impl.ConditionHasMana
import com.willfp.libreforge.integrations.auraskills.impl.EffectAddStat
import com.willfp.libreforge.integrations.auraskills.impl.EffectSkillXpMultiplier

object AuraSkillsIntegration : LoadableIntegration {

    override fun load(plugin: EcoPlugin) {
        if (plugin.server.pluginManager.getPlugin("EcoSkills") != null) {
            return
        }

        Effects.register(EffectAddStat)
        Conditions.register(ConditionHasMana)
        Effects.register(EffectSkillXpMultiplier)
        EffectArguments.register(ArgumentManaCost)
    }

    override fun getPluginName(): String {
        return "AuraSkills"
    }
}