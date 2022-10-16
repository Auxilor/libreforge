package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterPet : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val pet = (data.event as? WrappedPetXpEvent)?.pet
            ?: (data.event as? WrappedPetLevelUpEvent)?.pet
            ?: return true

        return config.withInverse("pet", Config::getStrings) {
            it.any { petName ->
                petName.equals(pet.id, ignoreCase = true)
            }
        }
    }
}
