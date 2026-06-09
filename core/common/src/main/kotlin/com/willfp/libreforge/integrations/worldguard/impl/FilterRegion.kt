package com.willfp.libreforge.integrations.worldguard.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterRegion : Filter<NoCompileData, List<String>>("region") {
    override val description = "Matches when the event occurs in one of the given WorldGuard region IDs."
    override val categories = setOf("world")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a region event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): List<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: List<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? RegionEvent ?: return true

        return value.containsIgnoreCase(event.region)
    }
}
