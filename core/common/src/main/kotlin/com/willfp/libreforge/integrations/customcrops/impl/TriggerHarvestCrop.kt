package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.momirealms.customcrops.api.event.CropBreakEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerHarvestCrop : Trigger("harvest_crop") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CropBreakEvent) {
        val player = event.entityBreaker() as? Player ?: return
        val location = event.location() ?: return
        val crop = event.cropConfig().id() ?: return

        println("Harvested crop: $crop")
        println("Location: $location")
        println("Stage: ${event.cropStageItemID()}")
        println("Stages: ${event.cropConfig().stageIDs()}")

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                text = crop,
                location = location
            )
        )
    }
}