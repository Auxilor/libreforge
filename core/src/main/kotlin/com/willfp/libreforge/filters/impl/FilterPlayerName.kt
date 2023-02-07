package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterPlayerName : Filter<NoCompileData, Collection<String>>("player_name") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun filter(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true
        return value.containsIgnoreCase(player.name)
    }
}
