package com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customcrops.api.event.CropBreakEvent
import net.momirealms.customcrops.api.event.CropPlantEvent

object FilterCustomCropType : Filter<NoCompileData, Collection<String>>("custom_crop_type") {
    override val description = "Matches when the crop type ID matches one of the given IDs."
    override val categories = setOf("world")
    override val valueType = ArgType.STRING_LIST

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val cropId = when (val event = data.event) {
            is CropBreakEvent -> event.cropConfig().id()
            is CropPlantEvent -> event.cropConfig().id()
            else -> return true
        }

        return value.contains(cropId)
    }
}