package com.willfp.libreforge.integrations.customcrops

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.customcrops.impl.FilterCropType
import com.willfp.libreforge.integrations.customcrops.impl.FilterFertilizerType
import com.willfp.libreforge.integrations.customcrops.impl.FilterFullyGrown
import com.willfp.libreforge.integrations.customcrops.impl.FilterWateringCanType
import com.willfp.libreforge.integrations.customcrops.impl.TriggerBonemealCrop
import com.willfp.libreforge.integrations.customcrops.impl.TriggerHarvestCrop
import com.willfp.libreforge.integrations.customcrops.impl.TriggerPlantCrop
import com.willfp.libreforge.integrations.customcrops.impl.TriggerUseFertilizer
import com.willfp.libreforge.integrations.customcrops.impl.TriggerUseWateringCan
import com.willfp.libreforge.triggers.Triggers

object CustomCropsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Filters.register(FilterCropType)
        Filters.register(FilterFertilizerType)
        Filters.register(FilterFullyGrown)
        Filters.register(FilterWateringCanType)
        Triggers.register(TriggerHarvestCrop)
        Triggers.register(TriggerPlantCrop)
        Triggers.register(TriggerBonemealCrop)
        Triggers.register(TriggerUseFertilizer)
        Triggers.register(TriggerUseWateringCan)
    }

    override fun getPluginName(): String {
        return "CustomCrops"
    }
}