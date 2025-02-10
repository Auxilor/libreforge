package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customcrops.api.event.CropBreakEvent
import net.momirealms.customcrops.api.event.CropPlantEvent
import net.momirealms.customcrops.api.event.FertilizerUseEvent

object FilterFullyGrown : Filter<NoCompileData, Boolean>("fully_grown") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val event = data.event as? CropBreakEvent ?: return false
        val cropConfig = event.cropConfig() ?: return false
        val currentStageID = event.cropStageItemID() ?: return false
        val finalStageID = cropConfig.stageIDs().lastOrNull() ?: return false

        return (currentStageID == finalStageID) == value
    }
}