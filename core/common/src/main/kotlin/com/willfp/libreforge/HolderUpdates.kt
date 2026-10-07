package com.willfp.libreforge

import com.willfp.eco.core.events.ArmorChangeEvent
import com.willfp.libreforge.slot.ItemScope
import com.willfp.libreforge.slot.SlotType
import com.willfp.libreforge.slot.impl.NumericSlotType
import com.willfp.libreforge.slot.impl.SlotTypeBoots
import com.willfp.libreforge.slot.impl.SlotTypeChestplate
import com.willfp.libreforge.slot.impl.SlotTypeHelmet
import com.willfp.libreforge.slot.impl.SlotTypeLeggings
import com.willfp.libreforge.slot.impl.SlotTypeMainhand
import com.willfp.libreforge.slot.impl.SlotTypeOffhand
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
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * Signals [HolderChange.Items] on item changes. Every change is applied in the next tick, after the
 * event has completed, so listener priority does not affect correctness.
 */
@Suppress("unused", "UNUSED_PARAMETER")
object ItemRefreshListener : Listener {
    private fun Entity.signalItems(scope: SignalScope? = null) =
        HolderStates.signal(this.toDispatcher(), HolderChange.Items, scope)

    private val armorSlots = listOf(SlotTypeHelmet, SlotTypeChestplate, SlotTypeLeggings, SlotTypeBoots) +
            (36..39).map { NumericSlotType(it) }

    // Using up a plain item can't change holders when refresh.held.require-meta is on.
    private fun isPlain(item: ItemStack?): Boolean =
        plugin.configYml.getBool("refresh.held.require-meta") && (item == null || !item.hasItemMeta())

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

        event.entity.signalItems(ItemScope.ofTypes(listOf(event.item.itemStack.type)))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onRespawn(event: PlayerRespawnEvent) {
        HolderStates.signal(event.player.toDispatcher(), HolderChange.Respawn)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onInventoryDrop(event: PlayerDropItemEvent) {
        event.player.signalItems(ItemScope.ofTypes(listOf(event.itemDrop.itemStack.type)))
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

        player.signalItems(
            ItemScope.ofSlots(SlotTypeMainhand, NumericSlotType(event.previousSlot), NumericSlotType(event.newSlot))
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onArmorChange(event: ArmorChangeEvent) {
        event.player.signalItems(ItemScope.of(armorSlots, emptyList()))
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSwapHands(event: PlayerSwapHandItemsEvent) {
        val player = event.player

        player.signalItems(
            ItemScope.ofSlots(SlotTypeMainhand, SlotTypeOffhand, NumericSlotType(player.inventory.heldItemSlot))
        )
    }

    // Cancelled clicks and drags count too: GUI plugins cancel them and move the items themselves.
    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        // Rate limited by refresh.inventory-click.timeout; a click inside the window is delayed, not dropped.
        HolderStates.signalInventoryClick(player.toDispatcher(), clickScope(event, player))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked as? Player ?: return

        val scope = if (isOwnInventory(event.view)) {
            ItemScope.of(listOf(SlotTypeMainhand, SlotTypeOffhand), listOf(event.oldCursor.type))
        } else {
            null
        }

        HolderStates.signalInventoryClick(player.toDispatcher(), scope)
    }

    // In another inventory, a GUI plugin may change any item itself, so everything is re-checked.
    private fun isOwnInventory(view: InventoryView): Boolean =
        view.topInventory.type == InventoryType.CRAFTING

    private fun clickScope(event: InventoryClickEvent, player: Player): SignalScope? {
        if (event is InventoryCreativeEvent || !isOwnInventory(event.view)) {
            return null
        }

        val slots = mutableListOf<SlotType>(SlotTypeMainhand, SlotTypeOffhand)
        val types = mutableListOf(event.cursor.type)

        event.currentItem?.let { types += it.type }

        if (event.clickedInventory === player.inventory) {
            slots += NumericSlotType(event.slot)
        }

        if (event.hotbarButton >= 0) {
            slots += NumericSlotType(event.hotbarButton)
            player.inventory.getItem(event.hotbarButton)?.let { types += it.type }
        }

        return ItemScope.of(slots, types)
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
        if (isPlain(event.itemInHand)) {
            return
        }

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
        // Mobs' equipment changes are signalled by the equipment listener, or picked up by polling.
        val player = event.entity as? Player ?: return

        if (isPlain(event.consumable)) {
            return
        }

        player.signalItems()
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        event.entity.signalItems()
    }
}
