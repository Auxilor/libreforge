package com.willfp.libreforge.integrations.citizens.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.citizensnpcs.api.event.NPCEvent

object FilterNPC : Filter<NoCompileData, Collection<Int>>("npc") {
    override val description = "Matches when the Citizens NPC involved in the event has one of the given IDs."
    override val categories = setOf("entity")
    override val valueType = ArgType.INT_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a Citizens NPC event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<Int> {
        return config.getInts(key)
    }

    override fun isMet(data: TriggerData, value: Collection<Int>, compileData: NoCompileData): Boolean {
        val event = data.event as? NPCEvent ?: return true

        return value.contains(event.npc.id)
    }
}
