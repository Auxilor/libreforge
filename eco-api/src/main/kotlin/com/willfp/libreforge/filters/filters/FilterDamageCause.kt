package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterDamageCause: FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val cause = data.damageCause ?: return true

        val causeNames = config.getStringsOrNull("damageCause", false)
            ?.map { it.uppercase() } ?: emptyList()

        return causeNames.contains(cause.name)
    }
}
