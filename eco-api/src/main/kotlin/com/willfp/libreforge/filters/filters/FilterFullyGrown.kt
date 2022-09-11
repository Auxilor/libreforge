package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.data.Ageable

class FilterFullyGrown : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val block = data.block ?: return true

        return config.withInverse("fully_grown", Config::getBoolOrNull) {
            val blockData = block.blockData

            val isFullyGrown = if (blockData is Ageable) {
                blockData.age == blockData.maximumAge
            } else {
                true
            }

            isFullyGrown == it
        }
    }
}
