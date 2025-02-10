package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customcrops.api.event.CropBreakEvent
import net.momirealms.customcrops.api.event.CropPlantEvent

object FilterCropType : Filter<NoCompileData, Collection<String>>("crop_type") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        return when (val event = data.event) {
            is CropBreakEvent -> {
                value.contains(event.cropConfig().id())
            }
            is CropPlantEvent -> {
                value.contains(event.cropConfig().id())
            }
            else -> true
        }
    }
}
