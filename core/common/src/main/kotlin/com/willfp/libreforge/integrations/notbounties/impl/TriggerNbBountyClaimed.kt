package com.willfp.libreforge.integrations.notbounties.impl

import com.willfp.libreforge.integrations.notbounties.listenForBountyClaim
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.jadenp.notbounties.bounty_events.BountyClaimEvent
import org.bukkit.Bukkit

object TriggerNbBountyClaimed : Trigger("nb_bounty_claimed") {
    override val description = "Fires when another player claims the player's NotBounties bounty."

    override val categories = setOf("combat")

    override val additionalInfo = listOf("Requires the NotBounties plugin.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The player who claimed the bounty.",
        TriggerParameter.VALUE to "The amount of the bounty that was claimed.",
        TriggerParameter.TEXT to "The name of the player who claimed the bounty."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE,
        TriggerParameter.TEXT
    )

    override fun postRegister() {
        listenForBountyClaim { handle(it) }
    }

    private fun handle(event: BountyClaimEvent) {
        val killer = event.killer ?: return
        val bounty = event.bounty
        val player = Bukkit.getPlayer(bounty.uuid) ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = killer,
                event = event,
                value = bounty.getTotalDisplayBounty(killer),
                text = killer.name
            )
        )
    }
}
