package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent

object TriggerClickBlock : Trigger("click_block") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val player = event.player

        this.dispatch(
            player,
            TriggerData(
                player = player,
                location = block.location,
                block = block,
                event = event
            )
        )
    }
}
