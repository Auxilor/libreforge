package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecopets.api.event.PlayerPetExpGainEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerGainPetXp : Trigger(
    "gain_pet_xp", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.EVENT
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerPetExpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location,
                event = WrappedPetXpEvent(event)
            ),
            event.amount
        )
    }
}
