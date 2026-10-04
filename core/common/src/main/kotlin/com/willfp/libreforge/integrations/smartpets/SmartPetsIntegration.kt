package com.willfp.libreforge.integrations.smartpets

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.smartpets.impl.ConditionSpActivePetType
import com.willfp.libreforge.integrations.smartpets.impl.ConditionSpEvolutionStageAbove
import com.willfp.libreforge.integrations.smartpets.impl.ConditionSpHasActivePet
import com.willfp.libreforge.integrations.smartpets.impl.ConditionSpHasSkill
import com.willfp.libreforge.integrations.smartpets.impl.ConditionSpPetBehavior
import com.willfp.libreforge.integrations.smartpets.impl.ConditionSpPetLevelAbove
import com.willfp.libreforge.integrations.smartpets.impl.ConditionSpPetLevelBelow
import com.willfp.libreforge.integrations.smartpets.impl.ConditionSpStatAbove
import com.willfp.libreforge.integrations.smartpets.impl.ConditionSpStatBelow
import com.willfp.libreforge.integrations.smartpets.impl.EffectSpEvolvePet
import com.willfp.libreforge.integrations.smartpets.impl.EffectSpGiveStat
import com.willfp.libreforge.integrations.smartpets.impl.EffectSpGiveXp
import com.willfp.libreforge.integrations.smartpets.impl.EffectSpUnlockSkill
import com.willfp.libreforge.integrations.smartpets.impl.EffectSpXpMultiplier
import com.willfp.libreforge.integrations.smartpets.impl.FilterSpCause
import com.willfp.libreforge.integrations.smartpets.impl.FilterSpInteraction
import com.willfp.libreforge.integrations.smartpets.impl.FilterSpInteractionPositive
import com.willfp.libreforge.integrations.smartpets.impl.FilterSpInteractionResult
import com.willfp.libreforge.integrations.smartpets.impl.FilterSpPetId
import com.willfp.libreforge.integrations.smartpets.impl.FilterSpPetType
import com.willfp.libreforge.integrations.smartpets.impl.FilterSpSkill
import com.willfp.libreforge.integrations.smartpets.impl.FilterSpStat
import com.willfp.libreforge.integrations.smartpets.impl.FilterSpXpSource
import com.willfp.libreforge.integrations.smartpets.impl.TriggerSpAdoptPet
import com.willfp.libreforge.integrations.smartpets.impl.TriggerSpEvolve
import com.willfp.libreforge.integrations.smartpets.impl.TriggerSpGainStat
import com.willfp.libreforge.integrations.smartpets.impl.TriggerSpGainXp
import com.willfp.libreforge.integrations.smartpets.impl.TriggerSpInteract
import com.willfp.libreforge.integrations.smartpets.impl.TriggerSpLevelUp
import com.willfp.libreforge.integrations.smartpets.impl.TriggerSpLoseStat
import com.willfp.libreforge.integrations.smartpets.impl.TriggerSpRemovePet
import com.willfp.libreforge.integrations.smartpets.impl.TriggerSpUnlockSkill
import com.willfp.libreforge.triggers.Triggers

object SmartPetsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerSpAdoptPet)
        Triggers.register(TriggerSpEvolve)
        Triggers.register(TriggerSpGainStat)
        Triggers.register(TriggerSpGainXp)
        Triggers.register(TriggerSpInteract)
        Triggers.register(TriggerSpLevelUp)
        Triggers.register(TriggerSpLoseStat)
        Triggers.register(TriggerSpRemovePet)
        Triggers.register(TriggerSpUnlockSkill)
        Filters.register(FilterSpCause)
        Filters.register(FilterSpInteraction)
        Filters.register(FilterSpInteractionPositive)
        Filters.register(FilterSpInteractionResult)
        Filters.register(FilterSpPetId)
        Filters.register(FilterSpPetType)
        Filters.register(FilterSpSkill)
        Filters.register(FilterSpStat)
        Filters.register(FilterSpXpSource)
        Conditions.register(ConditionSpActivePetType)
        Conditions.register(ConditionSpEvolutionStageAbove)
        Conditions.register(ConditionSpHasActivePet)
        Conditions.register(ConditionSpHasSkill)
        Conditions.register(ConditionSpPetBehavior)
        Conditions.register(ConditionSpPetLevelAbove)
        Conditions.register(ConditionSpPetLevelBelow)
        Conditions.register(ConditionSpStatAbove)
        Conditions.register(ConditionSpStatBelow)
        Effects.register(EffectSpEvolvePet)
        Effects.register(EffectSpGiveStat)
        Effects.register(EffectSpGiveXp)
        Effects.register(EffectSpUnlockSkill)
        Effects.register(EffectSpXpMultiplier)
        plugin.eventManager.registerListener(SmartPetsRefreshListener)
    }

    override fun getPluginName(): String {
        return "SmartPets"
    }
}
