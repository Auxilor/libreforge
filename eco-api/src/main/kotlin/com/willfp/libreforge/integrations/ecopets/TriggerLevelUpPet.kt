package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecopets.api.event.PlayerPetLevelUpEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler

class TriggerLevelUpPet : Trigger(
    "level_up_pet", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerPetLevelUpEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                location = player.location
            ),
            event.level.toDouble()
        )
    }
}
