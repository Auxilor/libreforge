package com.willfp.libreforge.integrations.xiaomomiplugins.customcrops.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.momirealms.customcrops.api.event.WateringCanWaterPotEvent
import net.momirealms.customcrops.api.event.WateringCanWaterSprinklerEvent
import org.bukkit.event.EventHandler

object TriggerUseWateringCan : Trigger("use_watering_can") {
    override val description = "Fires when the player uses a CustomCrops watering can."

    override val categories = setOf("world", "interaction")

    override val additionalInfo = listOf("Requires CustomCrops (xiaomomi) to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The location that was watered."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: WateringCanWaterSprinklerEvent) {
        val player = event.player ?: return
        val location = event.location() ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location
            )
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: WateringCanWaterPotEvent) {
        val player = event.player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
            )
        )
    }
}