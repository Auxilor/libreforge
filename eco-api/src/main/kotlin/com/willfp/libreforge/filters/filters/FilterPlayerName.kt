package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterPlayerName : Filter() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val player = data.player ?: return true

        return config.withInverse("player_name", Config::getStrings) {
            it.containsIgnoreCase(player.name)
        }
    }
}
