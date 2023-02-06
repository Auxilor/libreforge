package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecopets.api.event.PlayerPetExpGainEvent
import com.willfp.ecopets.pets.Pet
import com.willfp.ecopets.pets.Pets
import com.willfp.libreforge.effects.GenericMultiMultiplierEffect
import org.bukkit.event.EventHandler

class EffectPetXpMultiplier : GenericMultiMultiplierEffect<Pet>(
    "pet_xp_multiplier",
    Pets::getByID,
    Pets::values,
    "pets"
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerPetExpGainEvent) {
        val player = event.player

        val multiplier = getMultiplier(player, event.pet)

        val wrapped = WrappedPetXpEvent(event)
        wrapped.amount = wrapped.amount * multiplier
    }
}
