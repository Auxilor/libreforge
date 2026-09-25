package com.willfp.libreforge.integrations.notbounties.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.jadenp.notbounties.bounty_events.BountySetEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object TriggerNbReceiveBounty : Trigger("nb_receive_bounty") {
    override val description = "Fires when a NotBounties bounty is set on the player."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the NotBounties plugin.",
        "Only fires when the player is online."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The player who set the bounty, if it was not set by the console.",
        TriggerParameter.VALUE to "The amount of the bounty that was set.",
        TriggerParameter.TEXT to "The name of the player who set the bounty."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: BountySetEvent) {
        val bounty = event.bounty
        val receiver = Bukkit.getPlayer(bounty.uuid) ?: return
        val setter = bounty.lastSetter

        this.dispatch(
            receiver.toDispatcher(),
            TriggerData(
                player = receiver,
                victim = setter?.uuid?.let { Bukkit.getPlayer(it) },
                event = event,
                value = bounty.totalDisplayBounty,
                text = setter?.name
            )
        )
    }
}
