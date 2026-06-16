package com.willfp.libreforge.integrations.factionsbridge.impl.filters

import cc.javajobs.factionsbridge.bridge.events.FactionLeaveEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterLeaveReason : Filter<NoCompileData, Collection<String>>("leave_reason") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? FactionLeaveEvent ?: return true
        return value.any { it.equals(event.getReason().name, ignoreCase = true) }
    }
}
