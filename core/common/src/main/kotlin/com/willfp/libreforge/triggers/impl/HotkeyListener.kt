package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.events.PlayerOpenAdvancementsEvent
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Listens for every key behind [TriggerHotkey], doing nothing for a key unless its trigger's
 * option is enabled.
 *
 * While a trigger is enabled, the key's normal action is always cancelled once it has fired,
 * and the advancements screen is closed.
 */
object HotkeyListener : Listener {
    private val inventoryDropActions = setOf(
        InventoryAction.DROP_ALL_CURSOR,
        InventoryAction.DROP_ONE_CURSOR,
        InventoryAction.DROP_ALL_SLOT,
        InventoryAction.DROP_ONE_SLOT
    )

    private val inventoryDrops = ConcurrentHashMap.newKeySet<UUID>()

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        val trigger = TriggerHotkey.of(Hotkey.SWAP_HANDS, player.isSneaking)

        if (!trigger.option.isEnabled) {
            return
        }

        trigger.fire(player, event, player.inventory.itemInMainHand)
        event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerItemHeldEvent) {
        val hotkey = Hotkey.slot(event.newSlot) ?: return
        val player = event.player
        val trigger = TriggerHotkey.of(hotkey, player.isSneaking)

        if (!trigger.option.isEnabled) {
            return
        }

        trigger.fire(player, event, player.inventory.getItem(event.newSlot))
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: InventoryClickEvent) {
        if (!TriggerHotkey.isAnyEnabled(Hotkey.DROP)) {
            return
        }

        if (event.action !in inventoryDropActions) {
            return
        }

        val player = event.whoClicked as? Player ?: return

        inventoryDrops += player.uniqueId
        plugin.scheduler.on(player).run {
            inventoryDrops -= player.uniqueId
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerDropItemEvent) {
        val player = event.player
        val trigger = TriggerHotkey.of(Hotkey.DROP, player.isSneaking)

        if (!trigger.option.isEnabled) {
            return
        }

        if (inventoryDrops.remove(player.uniqueId)) {
            return
        }

        trigger.fire(player, event, event.itemDrop.itemStack)
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerOpenAdvancementsEvent) {
        val player = event.player
        val trigger = TriggerHotkey.of(Hotkey.ADVANCEMENTS, player.isSneaking)

        if (!trigger.option.isEnabled) {
            return
        }

        plugin.scheduler.on(player).run {
            trigger.fire(player, null, player.inventory.itemInMainHand)
            player.closeInventory()
        }
    }

    private fun TriggerHotkey.fire(player: Player, event: Event?, item: ItemStack?) {
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                item = item
            )
        )
    }
}
