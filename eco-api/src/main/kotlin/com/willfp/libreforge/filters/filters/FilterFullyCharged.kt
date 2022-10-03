package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.wrappers.WrappedMeleeDamageEvent

class FilterFullyCharged : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val event = data.event as? WrappedMeleeDamageEvent ?: return true

        return config.withInverse("fully_charged", Config::getBoolOrNull) {
            event.isFullyCharged == it
        }
    }
}
