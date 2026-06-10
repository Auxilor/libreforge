package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.CauldronLevelChangeEvent

object TriggerCauldronLevelChange : Trigger("cauldron_level_change") {
    override val description = "Fires when the player changes the water level of a cauldron."

    override val categories = setOf("interaction")

    override val parameterDescriptions = mapOf(
        TriggerParameter.BLOCK to "The cauldron block.",
        TriggerParameter.LOCATION to "The cauldron's location.",
        TriggerParameter.VALUE to "The new water level of the cauldron after the change."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: CauldronLevelChangeEvent) {
        val player = event.entity as? Player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                block = event.block,
                location = event.block.location,
                value = event.newLevel.toDouble(),
                event = event
            )
        )
    }
}
