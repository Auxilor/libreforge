package com.willfp.libreforge.integrations.aureliumskills

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.arguments.EffectArguments
import com.willfp.libreforge.integrations.aureliumskills.impl.ConditionHasMana
import com.willfp.libreforge.integrations.aureliumskills.impl.EffectAddStat
import com.willfp.libreforge.integrations.aureliumskills.impl.EffectArgumentManaCost
import com.willfp.libreforge.integrations.aureliumskills.impl.EffectSkillXpMultiplier

object AureliumSkillsIntegration : Integration {
    fun load() {
        Effects.register(EffectAddStat)
        Conditions.register(ConditionHasMana)
        Effects.register(EffectSkillXpMultiplier)
        EffectArguments.register(EffectArgumentManaCost)
    }

    override fun getPluginName(): String {
        return "AureliumSkills"
    }
}
