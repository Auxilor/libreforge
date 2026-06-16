package com.willfp.libreforge.integrations.factionsbridge.impl.filters

import cc.javajobs.factionsbridge.FactionsBridge
import cc.javajobs.factionsbridge.bridge.infrastructure.struct.Role
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

private val RANK = mapOf(
    Role.RECRUIT to 0,
    Role.NORMAL to 1,
    Role.OFFICER to 2,
    Role.CO_LEADER to 3,
    Role.LEADER to 4
)

object FilterFactionRoleAtLeast : Filter<NoCompileData, Collection<String>>("faction_role_at_least") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true
        val api = FactionsBridge.getFactionsAPI() ?: return true
        val fplayer = api.getFPlayer(player) ?: return true
        val required = value.firstOrNull()?.let { Role.getRole(it.uppercase()) } ?: return true
        val requiredRank = RANK[required] ?: return true
        val playerRank = RANK[fplayer.getRole()] ?: return false
        return playerRank >= requiredRank
    }
}
