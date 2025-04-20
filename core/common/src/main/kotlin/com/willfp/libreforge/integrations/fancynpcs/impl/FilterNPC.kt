package com.willfp.libreforge.integrations.fancynpcs.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import de.oliver.fancynpcs.api.events.NpcInteractEvent

object FilterNPC : Filter<NoCompileData, Collection<String>>("npc") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? NpcInteractEvent ?: return true

        return value.contains(event.npc.data.id)
    }
}