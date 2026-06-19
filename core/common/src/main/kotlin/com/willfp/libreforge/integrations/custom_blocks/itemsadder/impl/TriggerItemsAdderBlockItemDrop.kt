package com.willfp.libreforge.integrations.custom_blocks.itemsadder.impl

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
import com.willfp.libreforge.triggers.impl.TriggerBlockItemDrop
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object TriggerItemsAdderBlockItemDrop : Listener {
    private data class PendingBreak(val playerUuid: UUID, val items: MutableList<ItemStack>)

    private val pending = ConcurrentHashMap<Location, PendingBreak>()
    private val reDropping = ThreadLocal.withInitial { false }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: CustomBlockBreakEvent) {
        if (!TriggerBlockItemDrop.isEnabled) return

        val player = event.player
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return
        if (!AntigriefManager.canBreakBlock(player, event.block)) return

        val loc = event.block.location
        val entry = PendingBreak(player.uniqueId, mutableListOf())
        pending[loc] = entry

        plugin.scheduler.runLater(1) {
            val break_ = pending.remove(loc) ?: return@runLater
            processDrops(loc, break_.playerUuid, break_.items)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun handle(event: ItemSpawnEvent) {
        if (reDropping.get()) return

        val itemLoc = event.entity.location
        val world = itemLoc.world ?: return

        val entry = pending.entries.firstOrNull { (blockLoc, _) ->
            blockLoc.world == world &&
                itemLoc.distanceSquared(blockLoc.clone().add(0.5, 0.5, 0.5)) <= 4.0
        } ?: return

        event.isCancelled = true
        entry.value.items.add(event.entity.itemStack.clone())
    }

    private fun processDrops(loc: Location, playerUuid: UUID, collected: List<ItemStack>) {
        val items = collected.filterNotEmpty()
        if (items.isEmpty()) return

        val player = Bukkit.getPlayer(playerUuid) ?: return
        val block = loc.block

        val editableEvent = EditableDropEvent(
            initialDrops = items,
            cause = DropCause.BLOCK,
            context = DropContext(
                player = player,
                block = block,
                tool = player.inventory.itemInMainHand
            ),
            dropLocation = loc
        )

        TriggerBlockItemDrop.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                block = block,
                location = loc,
                event = editableEvent,
                item = null,
                value = items.sumOf { it.amount.toDouble() }
            )
        )

        val dropResults = editableEvent.items

        val world = loc.world ?: return
        reDropping.set(true)
        try {
            for (item in editableEvent.drops) {
                world.dropItemNaturally(loc, item)
            }
        } finally {
            reDropping.set(false)
        }

        val totalXP = dropResults.sumOf { it.xp }
        if (totalXP > 0) {
            DropQueue(player)
                .setLocation(loc)
                .addXP(totalXP)
                .push()
        }
    }
}
