package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.BrewEvent

class TriggerBrew : Trigger(
    "brew", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )
) {
    @EventHandler(ignoreCancelled = true)
    fun handle(event: BrewEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.contents.viewers.firstOrNull() as? Player ?: return
        val item = (0..2).map { event.contents.getItem(it) }
            .filterNot { EmptyTestableItem().matches(it) }
            .firstOrNull()

        this.dispatch(
            player,
            TriggerData(
                player = player,
                item = item
            )
        )
    }
}
