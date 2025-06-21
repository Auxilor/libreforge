package com.willfp.libreforge.integrations.lands.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.angeschossen.lands.api.events.land.bank.LandBankWithdrawEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

object TriggerLandsBankWithdraw : Trigger("lands_bank_withdraw") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: LandBankWithdrawEvent) {
        val player = event.landPlayer as Player
        val location = player.location
        val value = event.value

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = location,
                value = value
            )
        )
    }
}