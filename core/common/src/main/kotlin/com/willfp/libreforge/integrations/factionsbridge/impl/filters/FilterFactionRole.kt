package com.willfp.libreforge.integrations.factionsbridge.impl.filters

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterFactionRole : Filter<NoCompileData, Collection<String>>("faction_role") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true
        val api = FactionsBridge.getFactionsAPI() ?: return true
        val fplayer = api.getFPlayer(player) ?: return true
        return value.any { it.equals(fplayer.getRole().name, ignoreCase = true) }
    }
}
