package com.willfp.libreforge.integrations.notbounties.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.jadenp.notbounties.bounty_events.BountyRemoveEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object TriggerNbBuyBounty : Trigger("nb_buy_bounty") {
    override val description = "Fires when the player buys off their own NotBounties bounty."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires the NotBounties plugin.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VALUE to "The amount of the bounty that was bought off."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: BountyRemoveEvent) {
        if (!event.isBought) {
            return
        }

        val player = event.remover as? Player ?: return

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                value = event.bounty.totalDisplayBounty
            )
        )
    }
}
