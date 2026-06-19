package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.william278.husktowns.events.MemberLeaveEvent
import org.bukkit.event.EventHandler

object TriggerLeaveTown :Trigger("leave_town") {
    override val description = "Fires when the player leaves a HuskTowns town."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires HuskTowns to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The role name the player held when leaving."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: MemberLeaveEvent) {
        val player = event.player.player ?: return
        val role = event.memberRole.name ?: return

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