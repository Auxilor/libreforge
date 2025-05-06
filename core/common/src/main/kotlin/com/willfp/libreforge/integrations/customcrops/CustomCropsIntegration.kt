package com.willfp.libreforge.integrations.customcrops

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.customcrops.impl.*

import com.willfp.libreforge.triggers.Triggers

object CustomCropsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionIsSeason)
        Filters.register(FilterCustomCropStage)
        Filters.register(FilterCustomCropType)
        Filters.register(FilterFertilizerType)
        Filters.register(FilterWateringCanType)
        Triggers.register(TriggerHarvestCustomCrop)
        Triggers.register(TriggerPlantCustomCrop)
        Triggers.register(TriggerBonemealCustomCrop)
        Triggers.register(TriggerUseFertilizer)
        Triggers.register(TriggerUseWateringCan)
    }

    override fun getPluginName(): String {
        return "CustomCrops"
    }
}