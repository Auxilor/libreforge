package com.willfp.libreforge.integrations.AxPlugins.axenvoy.impl

import com.artillexstudios.axenvoy.event.EnvoyCrateCollectEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterEnvoyType : Filter<NoCompileData, Collection<String>>("envoy_type") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? EnvoyCrateCollectEvent ?: return true
        val crateType = event.crate?.handle?.name ?: return true

        return value.contains(crateType)
    }
}
