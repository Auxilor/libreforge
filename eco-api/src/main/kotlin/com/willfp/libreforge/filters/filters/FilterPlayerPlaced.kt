package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.isPlayerPlaced
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterPlayerPlaced : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val block = data.block ?: return true

        return config.withInverse("player_placed", Config::getBool) {
            block.isPlayerPlaced == it
        }
    }
}
