package com.willfp.libreforge.integrations.ecoskills

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ecoskills.impl.ConditionHasSkillLevel
import com.willfp.libreforge.integrations.ecoskills.impl.EffectAddStat
import com.willfp.libreforge.integrations.ecoskills.impl.EffectAddStatTemporarily
import com.willfp.libreforge.integrations.ecoskills.impl.EffectGiveSkillXp
import com.willfp.libreforge.integrations.ecoskills.impl.EffectMultiplyAllStats
import com.willfp.libreforge.integrations.ecoskills.impl.EffectMultiplyStat
import com.willfp.libreforge.integrations.ecoskills.impl.EffectMultiplyStatTemporarily
import com.willfp.libreforge.integrations.ecoskills.impl.EffectSkillXpMultiplier
import com.willfp.libreforge.integrations.ecoskills.impl.FilterSkill
import com.willfp.libreforge.integrations.ecoskills.impl.TriggerGainSkillXp
import com.willfp.libreforge.integrations.ecoskills.impl.TriggerLevelUpSkill
import com.willfp.libreforge.triggers.Triggers

object EcoSkillsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectAddStat)
        Effects.register(EffectMultiplyStat)
        Effects.register(EffectSkillXpMultiplier)
        Effects.register(EffectGiveSkillXp)
        Effects.register(EffectMultiplyAllStats)
        Effects.register(EffectAddStatTemporarily)
        Effects.register(EffectMultiplyStatTemporarily)
        Conditions.register(ConditionHasSkillLevel)
        Triggers.register(TriggerGainSkillXp)
        Triggers.register(TriggerLevelUpSkill)
        Filters.register(FilterSkill)
    }

    override fun getPluginName(): String {
        return "EcoSkills"
    }
}
