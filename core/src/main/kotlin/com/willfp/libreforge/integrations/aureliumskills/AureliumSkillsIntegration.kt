package com.willfp.libreforge.integrations.aureliumskills

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.arguments.EffectArguments
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.aureliumskills.impl.ArgumentManaCost
import com.willfp.libreforge.integrations.aureliumskills.impl.ConditionHasMana
import com.willfp.libreforge.integrations.aureliumskills.impl.EffectAddStat
import com.willfp.libreforge.integrations.aureliumskills.impl.EffectSkillXpMultiplier

object AureliumSkillsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectAddStat)
        Conditions.register(ConditionHasMana)
        Effects.register(EffectSkillXpMultiplier)
        EffectArguments.register(ArgumentManaCost)
    }

    override fun getPluginName(): String {
        return "AureliumSkills"
    }
}
