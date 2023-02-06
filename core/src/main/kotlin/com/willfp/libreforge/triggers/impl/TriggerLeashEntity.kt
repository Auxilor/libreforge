package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerLeashEntityEvent

object TriggerLeashEntity : Trigger("leash_entity") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerLeashEntityEvent) {
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = event.entity.location,
                victim = event.entity as? LivingEntity,
                event = event
            )
        )
    }
}
