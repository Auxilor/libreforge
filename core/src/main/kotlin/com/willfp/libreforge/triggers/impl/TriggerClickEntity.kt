package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEntityEvent

object TriggerClickEntity : Trigger("click_entity") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEntityEvent) {
        val entity = event.rightClicked
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = entity.location,
                victim = entity as? LivingEntity,
                event = event
            )
        )
    }
}
