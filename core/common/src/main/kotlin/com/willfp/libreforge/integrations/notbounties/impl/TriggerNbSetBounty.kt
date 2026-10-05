package com.willfp.libreforge.integrations.notbounties.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.jadenp.notbounties.bounty_events.BountySetEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object TriggerNbSetBounty : Trigger("nb_set_bounty") {
    override val description = "Fires when the player sets a NotBounties bounty on another player."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the NotBounties plugin.",
        "Does not fire for bounties set by the console."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The player the bounty was set on, if they are online.",
        TriggerParameter.VALUE to "The amount of the bounty that was set.",
        TriggerParameter.TEXT to "The name of the player the bounty was set on."
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
        val setter = Bukkit.getPlayer(bounty.lastSetter?.uuid ?: return) ?: return

        this.dispatch(
            setter.toDispatcher(),
            TriggerData(
                player = setter,
                victim = Bukkit.getPlayer(bounty.uuid),
                event = event,
                value = bounty.totalDisplayBounty,
                text = bounty.name
            )
        )
    }
}
