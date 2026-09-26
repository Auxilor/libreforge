package com.willfp.libreforge

import com.willfp.eco.core.events.ArmorChangeEvent
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

/**
 * Signals [HolderChange.Items] on item changes. Every change is applied in the next tick, after the
 * event has completed, so listener priority does not affect correctness.
 */
@Suppress("unused", "UNUSED_PARAMETER")
object ItemRefreshListener : Listener {
    private fun Entity.signalItems() =
        HolderStates.signal(this.toDispatcher(), HolderChange.Items)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onItemPickup(event: EntityPickupItemEvent) {
        if (!plugin.configYml.getBool("refresh.pickup.enabled")) {
            return
        }

        if (plugin.configYml.getBool("refresh.pickup.require-meta")) {
            if (!event.item.itemStack.hasItemMeta()) {
                return
            }
        }

        event.entity.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onRespawn(event: PlayerRespawnEvent) {
        HolderStates.signal(event.player.toDispatcher(), HolderChange.Respawn)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onInventoryDrop(event: PlayerDropItemEvent) {
        event.player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChangeSlot(event: PlayerItemHeldEvent) {
        val player = event.player

        if (plugin.configYml.getBool("refresh.held.require-meta")) {
            val oldItem = player.inventory.getItem(event.previousSlot)
            val newItem = player.inventory.getItem(event.newSlot)
            if (((oldItem == null) || !oldItem.hasItemMeta()) && ((newItem == null) || !newItem.hasItemMeta())) {
                return
            }
        }

        player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onArmorChange(event: ArmorChangeEvent) {
        event.player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSwapHands(event: PlayerSwapHandItemsEvent) {
        event.player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        // Rate limited by refresh.inventory-click.timeout; a click inside the window is delayed, not dropped.
        HolderStates.signalInventoryClick(player.toDispatcher())
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onInventoryDrag(event: InventoryDragEvent) {
        event.whoClicked.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onItemBreak(event: PlayerItemBreakEvent) {
        event.player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onItemConsume(event: PlayerItemConsumeEvent) {
        event.player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        event.player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        event.player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBucketFill(event: PlayerBucketFillEvent) {
        event.player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onShootBow(event: EntityShootBowEvent) {
        event.entity.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        event.entity.signalItems()
    }
}
