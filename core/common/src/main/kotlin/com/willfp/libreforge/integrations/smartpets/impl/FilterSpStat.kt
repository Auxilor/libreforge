package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetStatChangeEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterSpStat : Filter<NoCompileData, Collection<String>>("sp_stat") {
    override val description = "Matches when the SmartPets stat that changed is one of the given stats."
    override val categories = setOf("pets")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a matching SmartPets event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? PetStatChangeEvent ?: return true

        return value.containsIgnoreCase(event.stat.name)
    }
}
