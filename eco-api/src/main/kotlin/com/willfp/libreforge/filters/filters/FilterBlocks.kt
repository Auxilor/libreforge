package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterBlocks : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val block = data.block ?: return true

        return config.getStringsOrNull("blocks")
            ?.containsIgnoreCase(block.type.name) ?: true
    }
}
