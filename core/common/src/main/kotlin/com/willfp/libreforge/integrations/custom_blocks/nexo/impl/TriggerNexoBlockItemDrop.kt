package com.willfp.libreforge.integrations.custom_blocks.nexo.impl

import com.nexomc.nexo.api.events.custom_block.NexoBlockBreakEvent
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.drops.DropContribution
import com.willfp.libreforge.drops.LibreforgeDrops
import com.willfp.libreforge.filterNotEmpty
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.impl.TriggerBlockItemDrop
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

object TriggerNexoBlockItemDrop : Listener {
    private data class PendingBreak(
        val playerUuid: UUID,
        val items: MutableList<ItemStack>,
        val contribution: DropContribution
    )

    private val pending = ConcurrentHashMap<Location, PendingBreak>()
    private val reDropping = ThreadLocal.withInitial { false }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: NexoBlockBreakEvent) {
        if (!TriggerBlockItemDrop.isEnabled) return

        val player = event.player
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return
        if (!AntigriefManager.canBreakBlock(player, event.block)) return

        val loc = event.block.location
        val contribution = LibreforgeDrops.contributions(
            DropCause.BLOCK,
            DropContext(
                player = player,
                block = event.block,
                tool = player.inventory.itemInMainHand
            ),
            loc
        )
        pending[loc] = PendingBreak(player.uniqueId, mutableListOf(), contribution)

        plugin.scheduler.at(loc).runLater(1) {
            processDrops(loc, pending.remove(loc) ?: return@runLater)
        }
    }

    // A listener after ours can still cancel the break. Drop the pending entry so the
    // stored contribution is not delivered for a block that never broke.
    @EventHandler(priority = EventPriority.MONITOR)
    fun handleCancelled(event: NexoBlockBreakEvent) {
        if (event.isCancelled) pending.remove(event.block.location)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun handleItemSpawn(event: ItemSpawnEvent) {
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

    private fun processDrops(loc: Location, pendingBreak: PendingBreak) {
        val items = pendingBreak.items.filterNotEmpty()
        val contribution = pendingBreak.contribution
        if (items.isEmpty() && contribution.items.isEmpty() && contribution.xp == 0) return

        val player = Bukkit.getPlayer(pendingBreak.playerUuid) ?: return

        // The pipeline delivers through a DropQueue, which drops in the world
        // when the player has no telekinesis - guard so we don't re-collect our
        // own drops in the ItemSpawnEvent handler above.
        reDropping.set(true)
        try {
            LibreforgeDrops.dropAll(
                items,
                DropCause.BLOCK,
                DropContext(
                    player = player,
                    block = loc.block,
                    tool = player.inventory.itemInMainHand
                ),
                loc,
                contribution = contribution
            )
        } finally {
            reDropping.set(false)
        }
    }
}
