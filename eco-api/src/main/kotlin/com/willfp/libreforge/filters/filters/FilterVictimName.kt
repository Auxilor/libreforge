package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterVictimName : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val victim = data.victim ?: return true

        return config.withInverse("victim_name", Config::getStrings) {
            it.containsIgnoreCase(victim.name)
        }
    }
}
