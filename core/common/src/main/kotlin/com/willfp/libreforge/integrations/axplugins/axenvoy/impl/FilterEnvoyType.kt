package com.willfp.libreforge.integrations.axplugins.axenvoy.impl

import com.artillexstudios.axenvoy.event.EnvoyCrateCollectEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterEnvoyType : Filter<NoCompileData, Collection<String>>("envoy_type") {
    override val description = "Matches when the collected envoy crate type matches one of the given names."
    override val categories = setOf("player")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not an AxEnvoy crate collect event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? EnvoyCrateCollectEvent ?: return true
        val crateType = event.crate?.handle?.name ?: return true

        return value.contains(crateType)
    }
}
