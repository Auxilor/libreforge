package com.willfp.libreforge.integrations.factionsbridge.impl.filters

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

object FilterFactionRelation : Filter<NoCompileData, Collection<String>>("faction_relation") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true
        val victim = data.victim as? Player ?: return true
        val api = FactionsBridge.getFactionsAPI() ?: return true
        val playerFaction = api.getFaction(player) ?: return true
        val victimFaction = api.getFaction(victim) ?: return true
        val relation = playerFaction.getRelationshipTo(victimFaction)
        return value.any { it.equals(relation.name, ignoreCase = true) }
    }
}
