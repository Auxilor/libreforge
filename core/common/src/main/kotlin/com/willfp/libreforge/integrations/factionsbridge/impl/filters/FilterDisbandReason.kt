package com.willfp.libreforge.integrations.factionsbridge.impl.filters

import cc.javajobs.factionsbridge.bridge.events.FactionDisbandEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterDisbandReason : Filter<NoCompileData, Collection<String>>("disband_reason") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? FactionDisbandEvent ?: return true
        return value.any { it.equals(event.getReason().name, ignoreCase = true) }
    }
}
