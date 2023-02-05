package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityBreedEvent

class TriggerBreed : Trigger(
    "breed", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityBreedEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.breeder as? Player ?: return

        this.dispatch(
            player,
            TriggerData(
                player = player,
                victim = event.entity,
                location = event.entity.location,
                item = event.bredWith,
                value = event.experience.toDouble()
            )
        )
    }
}
