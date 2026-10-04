package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.SmartPetEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterSpPetType : Filter<NoCompileData, Collection<String>>("sp_pet_type") {
    override val description = "Matches when the type of the SmartPets pet in the event is one of the given types."
    override val categories = setOf("pets")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a matching SmartPets event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? SmartPetEvent ?: return true

        return value.containsIgnoreCase(event.pet.type)
    }
}
