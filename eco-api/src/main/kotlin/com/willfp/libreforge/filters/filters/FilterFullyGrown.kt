package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.data.Ageable

class FilterFullyGrown : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val block = data.block ?: return true

        return config.withInverse("fully_grown", Config::getBoolOrNull) {
            val data = block.blockData

            val isFullyGrown = if (data is Ageable) {
                data.age == data.maximumAge
            } else {
                true
            }

            isFullyGrown == it
        }
    }
}
