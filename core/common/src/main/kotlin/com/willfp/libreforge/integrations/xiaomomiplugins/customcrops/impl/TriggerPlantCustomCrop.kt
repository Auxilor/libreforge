package com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.momirealms.customcrops.api.event.CropPlantEvent
import org.bukkit.event.EventHandler

object TriggerPlantCustomCrop : Trigger("plant_custom_crop") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CropPlantEvent) {
        val player = event.player ?: return
        val location = event.location() ?: return
        val crop = event.cropConfig().id() ?: return

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