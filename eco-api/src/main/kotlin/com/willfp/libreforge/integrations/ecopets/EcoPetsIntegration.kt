package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.Trigger

object EcoPetsIntegration : Integration {
    private lateinit var HAS_PET_LEVEL: Condition
    private lateinit var PET_XP_MULTIPLIER: Effect
    private lateinit var HAS_ACTIVE_PET: Condition
    private lateinit var GAIN_PET_XP: Trigger
    private lateinit var GIVE_PET_XP: Effect
    private lateinit var LEVEL_UP_PET: Trigger
    private lateinit var PET: FilterComponent

    fun load() {
        HAS_PET_LEVEL = ConditionHasPetLevel()
        PET_XP_MULTIPLIER = EffectPetXpMultiplier()
        HAS_ACTIVE_PET = ConditionHasActivePet()
        GAIN_PET_XP = TriggerGainPetXp()
        GIVE_PET_XP = EffectGivePetXp()
        LEVEL_UP_PET = TriggerLevelUpPet()
        PET = FilterPet()
    }

    override fun getPluginName(): String {
        return "EcoPets"
    }
}