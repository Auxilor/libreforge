package com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customcrops.api.event.WateringCanFillEvent
import net.momirealms.customcrops.api.event.WateringCanWaterPotEvent
import net.momirealms.customcrops.api.event.WateringCanWaterSprinklerEvent

object FilterWateringCanType : Filter<NoCompileData, Collection<String>>("watering_can_type") {
    override val description = "Matches when the watering can used matches one of the given watering can IDs."
    override val categories = setOf("world")
    override val valueType = ArgType.STRING_LIST

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val wateringCanId = when (val event = data.event) {
            is WateringCanFillEvent -> event.wateringCanConfig().id()
            is WateringCanWaterSprinklerEvent -> event.wateringCanConfig().id()
            is WateringCanWaterPotEvent -> event.wateringCanConfig().id()
            else -> return true
        }

        return value.contains(wateringCanId)
    }
}