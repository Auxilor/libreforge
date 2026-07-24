package com.willfp.libreforge.integrations.custom_blocks.oraxen.impl

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.drops.LibreforgeDrops
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.impl.TriggerBlockItemDrop
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent
import io.th0rgal.oraxen.api.events.stringblock.OraxenStringBlockBreakEvent
import io.th0rgal.oraxen.utils.drops.Drop
import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object TriggerOraxenBlockItemDrop : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: OraxenNoteBlockBreakEvent) {
        val items = event.drop.loots.mapNotNull { it.itemStack }
        dispatchFor(event.player, event.block, items) { event.drop = Drop.emptyDrop() }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: OraxenStringBlockBreakEvent) {
        val items = event.drop.loots.mapNotNull { it.itemStack }
        dispatchFor(event.player, event.block, items) { event.drop = Drop.emptyDrop() }
    }

    private fun dispatchFor(player: Player, block: Block, rawItems: List<ItemStack>, clearDrop: () -> Unit) {
        if (!TriggerBlockItemDrop.isEnabled) return
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return
        if (!AntigriefManager.canBreakBlock(player, block)) return

        val items = rawItems.filterNotEmpty()
        if (items.isEmpty()) return

        clearDrop()

        LibreforgeDrops.dropAll(
            items,
            DropCause.BLOCK,
            DropContext(
                player = player,
                block = block,
                tool = player.inventory.itemInMainHand
            ),
            block.location
        )
    }
}
