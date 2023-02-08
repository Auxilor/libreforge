package com.willfp.libreforge.integrations.ecopets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecopets.api.event.PetEvent
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterPet : Filter<NoCompileData, Collection<String>>("pet") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun filter(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? PetEvent ?: return false

        return value.any { petName ->
            petName.equals(event.pet.id, ignoreCase = true)
        }
    }
}
