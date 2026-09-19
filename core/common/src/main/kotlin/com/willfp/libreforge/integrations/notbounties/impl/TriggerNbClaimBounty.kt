package com.willfp.libreforge.integrations.notbounties.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.jadenp.notbounties.bounty_events.BountyClaimEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object TriggerNbClaimBounty : Trigger("nb_claim_bounty") {
    override val description = "Fires when the player claims a NotBounties bounty by killing its target."

    override val categories = setOf("combat")

    override val additionalInfo = listOf("Requires the NotBounties plugin.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The player whose bounty was claimed.",
        TriggerParameter.VALUE to "The amount of the bounty the player claimed.",
        TriggerParameter.TEXT to "The name of the player whose bounty was claimed."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT
    )

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: BountyClaimEvent) {
        val killer = event.killer ?: return
        val bounty = event.bounty

        this.dispatch(
            killer.toDispatcher(),
            TriggerData(
                player = killer,
                victim = Bukkit.getPlayer(bounty.uuid),
                event = event,
                value = bounty.getTotalDisplayBounty(killer),
                text = bounty.name
            )
        )
    }
}
