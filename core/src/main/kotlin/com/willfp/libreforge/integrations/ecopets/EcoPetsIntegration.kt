package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ecopets.impl.ConditionHasActivePet
import com.willfp.libreforge.integrations.ecopets.impl.ConditionHasPetLevel
import com.willfp.libreforge.integrations.ecopets.impl.EffectGivePetXp
import com.willfp.libreforge.integrations.ecopets.impl.EffectPetXpMultiplier
import com.willfp.libreforge.integrations.ecopets.impl.FilterPet
import com.willfp.libreforge.integrations.ecopets.impl.TriggerGainPetXp
import com.willfp.libreforge.integrations.ecopets.impl.TriggerLevelUpPet
import com.willfp.libreforge.triggers.Triggers

object EcoPetsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasPetLevel)
        Conditions.register(ConditionHasActivePet)
        Effects.register(EffectPetXpMultiplier)
        Effects.register(EffectGivePetXp)
        Triggers.register(TriggerGainPetXp)
        Triggers.register(TriggerLevelUpPet)
        Filters.register(FilterPet)
    }

    override fun getPluginName(): String {
        return "EcoPets"
    }
}