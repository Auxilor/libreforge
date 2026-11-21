package com.willfp.libreforge.integrations.phoenixnpcs.impl

import com.phoenixplugins.phoenixnpcs.api.events.NpcEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterPhoenixNPC : Filter<NoCompileData, Collection<String>>("phoenix_npc") {
    override val description = "Matches when the PhoenixNPCs NPC involved in the event has one of the given IDs."
    override val categories = setOf("entity")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a PhoenixNPCs NPC event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> = config.getStrings(key)

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? NpcEvent ?: return true

        return value.contains(event.npc.identifier)
    }
}
