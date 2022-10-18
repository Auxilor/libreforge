package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterBlocks : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val block = data.block ?: return true

        return config.withInverse("blocks", Config::getStrings) {
            it.containsIgnoreCase(block.type.name)
        }
    }
}
