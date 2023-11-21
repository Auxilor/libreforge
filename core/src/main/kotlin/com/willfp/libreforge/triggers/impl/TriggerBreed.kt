package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityBreedEvent

object TriggerBreed : Trigger("breed") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityBreedEvent) {
        val player = event.breeder as? Player ?: return

        this.dispatch(
            player.toDispatcher(),
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
