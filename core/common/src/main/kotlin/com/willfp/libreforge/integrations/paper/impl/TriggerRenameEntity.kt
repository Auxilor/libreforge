package com.willfp.libreforge.integrations.paper.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.player.PlayerNameEntityEvent
import org.bukkit.event.EventHandler

object TriggerRenameEntity : Trigger("rename_entity") {
    override val description = "Fires when the player renames an entity with a name tag."

    override val categories = setOf("interaction")

    override val additionalInfo = listOf("Requires Paper to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was renamed.",
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.TEXT to "The new name applied to the entity."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VICTIM,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerNameEntityEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                victim = event.entity,
                text = event.name.toString(),
                event = event
            )
        )
    }
}
