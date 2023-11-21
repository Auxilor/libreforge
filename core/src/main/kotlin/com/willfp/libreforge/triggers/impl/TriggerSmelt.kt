package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.Furnace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.FurnaceSmeltEvent

object TriggerSmelt : Trigger("smelt") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FurnaceSmeltEvent) {
        val furnace = event.block.state as? Furnace ?: return
        val player = furnace.inventory.viewers.firstOrNull() as? Player ?: return
        val item = event.result

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                item = item,
                value = item.amount.toDouble(),
            )
        )
    }
}
