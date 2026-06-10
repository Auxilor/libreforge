package com.willfp.libreforge.integrations.paper.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.player.PlayerTradeEvent
import org.bukkit.event.EventHandler

object TriggerVillagerTrade : Trigger("villager_trade") {
    override val description = "Fires when the player completes a trade with a villager."

    override val categories = setOf("economy")

    override val additionalInfo = listOf("Requires Paper to be installed.")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The villager that was traded with.",
        TriggerParameter.ITEM to "The item received from the trade.",
        TriggerParameter.VALUE to "The villager experience gained from the trade."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerTradeEvent) {
        val player = event.player

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                victim = event.villager,
                item = event.trade.result,
                value = event.trade.villagerExperience.toDouble()
            )
        )
    }
}
