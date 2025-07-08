package com.willfp.libreforge.integrations.xiaomomiplugins.customcrops

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.ConditionIsSeason
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.FilterCustomCropStage
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.FilterCustomCropType
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.FilterFertilizerType
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.FilterWateringCanType
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.TriggerBonemealCrop
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.TriggerHarvestCustomCrop
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.TriggerPlantCustomCrop
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.TriggerUseFertilizer
import com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl.TriggerUseWateringCan

import com.willfp.libreforge.triggers.Triggers

object CustomCropsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionIsSeason)
        Filters.register(FilterCustomCropStage)
        Filters.register(FilterCustomCropType)
        Filters.register(FilterFertilizerType)
        Filters.register(FilterWateringCanType)
        Triggers.register(TriggerBonemealCrop)
        Triggers.register(TriggerHarvestCustomCrop)
        Triggers.register(TriggerPlantCustomCrop)
        Triggers.register(TriggerUseFertilizer)
        Triggers.register(TriggerUseWateringCan)
    }

    override fun getPluginName(): String {
        return "CustomCrops"
    }
}