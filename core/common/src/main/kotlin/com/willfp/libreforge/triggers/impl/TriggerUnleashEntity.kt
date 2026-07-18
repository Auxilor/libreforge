package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerUnleashEntityEvent


object TriggerUnleashEntity : Trigger("unleash_entity") {
    override val description = "Fires when the player removes a lead from a leashed entity."

    override val categories = setOf("interaction")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was unleashed.",
        TriggerParameter.LOCATION to "The entity's location."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerUnleashEntityEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = event.entity.location,
                victim = event.entity as? LivingEntity,
                event = event
            )
        )
    }
}
