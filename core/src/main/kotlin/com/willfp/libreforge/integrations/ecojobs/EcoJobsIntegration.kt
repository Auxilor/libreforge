package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ecojobs.impl.ConditionHasActiveJob
import com.willfp.libreforge.integrations.ecojobs.impl.ConditionHasJobLevel
import com.willfp.libreforge.integrations.ecojobs.impl.EffectGiveJobXp
import com.willfp.libreforge.integrations.ecojobs.impl.EffectJobXpMultiplier
import com.willfp.libreforge.integrations.ecojobs.impl.FilterJob
import com.willfp.libreforge.integrations.ecojobs.impl.TriggerGainJobXp
import com.willfp.libreforge.integrations.ecojobs.impl.TriggerJoinJob
import com.willfp.libreforge.integrations.ecojobs.impl.TriggerLeaveJob
import com.willfp.libreforge.integrations.ecojobs.impl.TriggerLevelUpJob
import com.willfp.libreforge.triggers.Triggers

object EcoJobsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasJobLevel)
        Conditions.register(ConditionHasActiveJob)
        Effects.register(EffectJobXpMultiplier)
        Effects.register(EffectGiveJobXp)
        Triggers.register(TriggerGainJobXp)
        Triggers.register(TriggerLevelUpJob)
        Triggers.register(TriggerJoinJob)
        Triggers.register(TriggerLeaveJob)
        Filters.register(FilterJob)
    }

    override fun getPluginName(): String {
        return "EcoJobs"
    }
}
