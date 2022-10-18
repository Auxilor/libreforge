package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.data.Ageable

object FilterFullyGrown : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val block = data.block ?: return true

        return config.withInverse("fully_grown", Config::getBool) {
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
