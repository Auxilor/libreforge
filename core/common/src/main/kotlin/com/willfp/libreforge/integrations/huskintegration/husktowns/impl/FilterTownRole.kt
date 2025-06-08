package com.willfp.libreforge.integrations.huskintegration.husktowns.impl


import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.william278.husktowns.events.MemberRoleChangeEvent

object FilterTownRole : Filter<NoCompileData, Collection<String>>("town_role") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? MemberRoleChangeEvent ?: return true

        return value.containsIgnoreCase(event.newRole.name)
    }
}