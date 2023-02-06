package com.willfp.libreforge.integrations.paper

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.triggers.GenericCancellableEvent
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.player.PlayerTradeEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerLeashEntityEvent

class TriggerVillagerTrade : Trigger(
    "villager_trade", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerTradeEvent) {
        val player = event.player

        this.processTrigger(
            player,
            TriggerData(
                player = player,
                victim = event.villager,
                item = event.trade.result
            ),
            event.trade.villagerExperience.toDouble()
        )
    }
}
