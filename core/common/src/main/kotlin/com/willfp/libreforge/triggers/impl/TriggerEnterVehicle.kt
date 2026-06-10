package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.vehicle.VehicleEnterEvent

object TriggerEnterVehicle : Trigger("enter_vehicle") {
    override val description = "Fires when the player enters a vehicle."

    override val categories = setOf("interaction", "movement")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The vehicle, if it is a living entity.",
        TriggerParameter.LOCATION to "The player's location."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: VehicleEnterEvent) {
        val player = event.entered as? Player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = event.vehicle as? LivingEntity,
                location = player.location,
                event = event
            )
        )
    }
}
