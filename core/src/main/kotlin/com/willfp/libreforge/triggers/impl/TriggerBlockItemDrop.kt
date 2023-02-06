package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.core.items.isEmpty
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.wrappers.WrappedBlockDropEvent
import org.bukkit.GameMode
import org.bukkit.block.Container
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockDropItemEvent

object TriggerBlockItemDrop : Trigger("block_item_drop") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BlockDropItemEvent) {
        val player = event.player
        val block = event.block

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (event.blockState is Container) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        val originalDrops = event.items.map { it.itemStack }.filterNot { it.isEmpty }

        this.dispatch(
            player,
            TriggerData(
                player = player,
                block = block,
                location = block.location,
                event = event,
                item = null
            ),
            originalDrops.sumOf { it.amount }.toDouble()
        )

        val newDrops = originalDrops.map(wrapped::modify)
        var xp = 0
        for ((_, i) in newDrops) {
            xp += i
        }

        for ((i, item) in event.items.withIndex()) {
            item.itemStack = newDrops[i].first
        }

        if (xp > 0) {
            DropQueue(player)
                .setLocation(block.location)
                .addXP(xp)
                .push()
        }
    }
}
