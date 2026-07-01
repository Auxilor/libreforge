package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.william278.husktowns.events.MemberJoinEvent

object FilterTownName : Filter<NoCompileData, Collection<String>>("town_name") {
    override val description = "Matches when the HuskTowns town name from the event matches one of the given names."
    override val categories = setOf("player")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a HuskTowns town event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? MemberJoinEvent ?: return true
        return value.containsIgnoreCase(event.town.name)
    }
}
