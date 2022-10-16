package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.attribute.Attribute

class FilterOnMaxHealth : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val entity = data.victim ?: return true

        return config.withInverse("on_max_health", Config::getBool) {
            val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value
            val onMaxHealth = entity.health == maxHealth

            onMaxHealth == it
        }
    }
}
