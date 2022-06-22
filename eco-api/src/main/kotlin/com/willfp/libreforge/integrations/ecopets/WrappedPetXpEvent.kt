package com.willfp.libreforge.integrations.ecopets

import com.willfp.ecopets.api.event.PlayerPetExpGainEvent
import com.willfp.ecopets.pets.Pet
import com.willfp.libreforge.triggers.WrappedEvent

class WrappedPetXpEvent(
    private val event: PlayerPetExpGainEvent
) : WrappedEvent<PlayerPetExpGainEvent> {
    var amount: Double
        get() = event.amount
        set(value) {
            event.amount = value
        }

    val pet: Pet
        get() = event.pet
}
