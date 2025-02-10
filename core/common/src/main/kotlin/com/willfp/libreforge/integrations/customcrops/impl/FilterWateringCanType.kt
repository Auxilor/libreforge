package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customcrops.api.event.CropBreakEvent
import net.momirealms.customcrops.api.event.CropPlantEvent
import net.momirealms.customcrops.api.event.WateringCanFillEvent
import net.momirealms.customcrops.api.event.WateringCanWaterPotEvent
import net.momirealms.customcrops.api.event.WateringCanWaterSprinklerEvent

object FilterWateringCanType : Filter<NoCompileData, Collection<String>>("crop_type") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        return when (val event = data.event) {
            is WateringCanFillEvent -> {
                value.contains(event.wateringCanConfig().id())
            }
            is WateringCanWaterSprinklerEvent -> {
                value.contains(event.wateringCanConfig().id())
            }
            is WateringCanWaterPotEvent -> {
                value.contains(event.wateringCanConfig().id())
            }
            else -> true
        }
    }
}
