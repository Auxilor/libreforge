package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterDamageCause: Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val cause = data.damageCause ?: return true

        return config.withInverse("damage_cause", Config::getStrings) {
            it.containsIgnoreCase(cause.name)
        }
    }
}
