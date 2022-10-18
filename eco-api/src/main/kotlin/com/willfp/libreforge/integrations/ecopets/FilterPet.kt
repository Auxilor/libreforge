package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

class FilterPet : Filter() {
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
