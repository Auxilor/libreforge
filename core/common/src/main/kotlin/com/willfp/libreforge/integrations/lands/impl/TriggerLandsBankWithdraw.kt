package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.land.bank.LandBankWithdrawEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerLandsBankWithdraw : Trigger("lands_bank_withdraw") {
    override val description = "Fires when the player withdraws money from a Lands bank."

    override val categories = setOf("economy")

    override val additionalInfo = listOf("Requires Lands to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location.",
        TriggerParameter.VALUE to "The amount withdrawn.",
        TriggerParameter.ALT_VALUE to "The new bank balance after the withdrawal."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandBankWithdrawEvent) {
        val landPlayer = event.landPlayer ?: return
        val player = Bukkit.getPlayer(landPlayer.uid) ?: return
        val location = player.location
        val value = event.value
        val balance = event.land.balance

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                value = value,
                altValue = balance - value
            )
        )
    }
}