package com.willfp.libreforge.integrations.paper.impl

import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.player.PlayerTradeEvent
import org.bukkit.event.EventHandler

object TriggerVillagerTrade : Trigger("villager_trade") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerTradeEvent) {
        val player = event.player

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                victim = event.villager,
                item = event.trade.result,
                value = event.trade.villagerExperience.toDouble()
            )
        )
    }
}
