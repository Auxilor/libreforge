package com.willfp.libreforge.integrations.auraskills

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.arguments.EffectArguments
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.auraskills.impl.ArgumentManaCost
import com.willfp.libreforge.integrations.auraskills.impl.ConditionHasMana
import com.willfp.libreforge.integrations.auraskills.impl.ConditionHasSkillLevel
import com.willfp.libreforge.integrations.auraskills.impl.ConditionHasStatLevel
import com.willfp.libreforge.integrations.auraskills.impl.EffectAddStat
import com.willfp.libreforge.integrations.auraskills.impl.EffectGiveMana
import com.willfp.libreforge.integrations.auraskills.impl.EffectGiveSkillXp
import com.willfp.libreforge.integrations.auraskills.impl.EffectGiveSkillXpNaturally
import com.willfp.libreforge.integrations.auraskills.impl.EffectManaRegenMultiplier
import com.willfp.libreforge.integrations.auraskills.impl.EffectMultiplyMana
import com.willfp.libreforge.integrations.auraskills.impl.EffectMultiplyStat
import com.willfp.libreforge.integrations.auraskills.impl.EffectSkillXpMultiplier
import com.willfp.libreforge.integrations.auraskills.impl.FilterManaAbility
import com.willfp.libreforge.integrations.auraskills.impl.FilterSkill
import com.willfp.libreforge.integrations.auraskills.impl.TriggerGainSkillXp
import com.willfp.libreforge.integrations.auraskills.impl.TriggerLevelUpSkill
import com.willfp.libreforge.integrations.auraskills.impl.TriggerManaAbilityActivate
import com.willfp.libreforge.integrations.auraskills.impl.TriggerRegenMana
import com.willfp.libreforge.triggers.Triggers

object AuraSkillsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        if (plugin.server.pluginManager.getPlugin("EcoSkills") != null) {
            return
        }

        Conditions.register(ConditionHasMana)
        Conditions.register(ConditionHasSkillLevel)
        Conditions.register(ConditionHasStatLevel)
        EffectArguments.register(ArgumentManaCost)
        Effects.register(EffectAddStat)
        Effects.register(EffectGiveMana)
        Effects.register(EffectGiveSkillXp)
        Effects.register(EffectGiveSkillXpNaturally)
        Effects.register(EffectManaRegenMultiplier)
        Effects.register(EffectMultiplyMana)
        Effects.register(EffectMultiplyStat)
        Effects.register(EffectSkillXpMultiplier)
        Filters.register(FilterManaAbility)
        Filters.register(FilterSkill)
        Triggers.register(TriggerGainSkillXp)
        Triggers.register(TriggerLevelUpSkill)
        Triggers.register(TriggerManaAbilityActivate)
        Triggers.register(TriggerRegenMana)
    }

    override fun getPluginName(): String {
        return "AuraSkills"
    }
}
