package com.willfp.libreforge.integrations.ecopets.impl

import com.willfp.ecopets.api.event.PlayerPetExpGainEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

object TriggerGainPetXp : Trigger("gain_pet_xp") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerPetExpGainEvent) {
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                value = event.amount
            )
        )
    }
}
