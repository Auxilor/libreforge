package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.isPlacedBy
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterSelfPlaced : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val block = data.block ?: return true
        val player = data.player ?: return true

        return config.withInverse("self_placed", Config::getBool) {
            block.isPlacedBy(player) == it
        }
    }
}
