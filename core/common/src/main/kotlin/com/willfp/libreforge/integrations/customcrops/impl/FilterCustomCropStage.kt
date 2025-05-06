package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customcrops.api.event.CropBreakEvent

object FilterCustomCropStage : Filter<NoCompileData, Collection<String>>("custom_crop_stage") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? CropBreakEvent ?: return true

        return value.contains(event.cropStageItemID())
    }
}