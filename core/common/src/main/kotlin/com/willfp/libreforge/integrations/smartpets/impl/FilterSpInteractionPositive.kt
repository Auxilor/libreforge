package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetInteractEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterSpInteractionPositive : Filter<NoCompileData, Boolean>("sp_interaction_positive") {
    override val description = "Matches when the SmartPets interaction was (or was not) a GREAT_SUCCESS or SUCCESS."
    override val categories = setOf("pets")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf("Passes automatically when the event is not a SmartPets interaction.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val event = data.event as? PetInteractEvent ?: return true

        return value == event.result.isPositive
    }
}
