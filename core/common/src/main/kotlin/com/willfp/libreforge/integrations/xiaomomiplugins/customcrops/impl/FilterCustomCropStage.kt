package com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customcrops.api.event.CropBreakEvent

object FilterCustomCropStage : Filter<NoCompileData, Collection<String>>("custom_crop_stage") {
    override val description = "Matches when the broken crop's stage item ID matches one of the given IDs."
    override val categories = setOf("world")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a CustomCrops crop break event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? CropBreakEvent ?: return true

        return value.contains(event.cropStageItemID())
    }
}