package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.husktowns.events.MemberRoleChangeEvent
import org.bukkit.event.EventHandler

object TriggerChangeTownRole : Trigger("change_town_role") {
    override val description = "Fires when the player's role in their HuskTowns town changes."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires HuskTowns to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The name of the new town role."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: MemberRoleChangeEvent) {
        val player = event.player.player ?: return
        val role = event.newRole.name ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                text = role
            )
        )
    }
}