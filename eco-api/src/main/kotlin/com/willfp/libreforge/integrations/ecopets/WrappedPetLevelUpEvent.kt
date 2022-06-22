package com.willfp.libreforge.integrations.ecopets

import com.willfp.ecopets.api.event.PlayerPetLevelUpEvent
import com.willfp.ecopets.pets.Pet
import com.willfp.libreforge.triggers.WrappedEvent

class WrappedPetLevelUpEvent(
    private val event: PlayerPetLevelUpEvent
) : WrappedEvent<PlayerPetLevelUpEvent> {
    val pet: Pet
        get() = event.pet
}
