package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customcrops.api.event.WateringCanFillEvent
import net.momirealms.customcrops.api.event.WateringCanWaterPotEvent
import net.momirealms.customcrops.api.event.WateringCanWaterSprinklerEvent

object FilterWateringCanType : Filter<NoCompileData, Collection<String>>("watering_can_type") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event
        val wateringCanId = when (event) {
            is WateringCanFillEvent -> event.wateringCanConfig().id()
            is WateringCanWaterSprinklerEvent -> event.wateringCanConfig().id()
            is WateringCanWaterPotEvent -> event.wateringCanConfig().id()
            else -> return true
        }

        return value.contains(wateringCanId)
    }
}