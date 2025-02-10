package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.momirealms.customcrops.api.event.CropBreakEvent
import net.momirealms.customcrops.api.event.CropPlantEvent
import net.momirealms.customcrops.api.event.FertilizerUseEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerUseFertilizer : Trigger("use_fertilizer") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FertilizerUseEvent) {
        val player = event.player ?: return
        val location = event.location() ?: return
        val fertilizer = event.fertilizer().id() ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                text = fertilizer,
                location = location
            )
        )
    }
}