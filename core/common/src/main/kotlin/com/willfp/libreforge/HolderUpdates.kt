package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.events.ArmorChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.UUID
import java.util.concurrent.TimeUnit

@Suppress("unused", "UNUSED_PARAMETER")
class ItemRefreshListener(
    private val plugin: EcoPlugin
) : Listener {
    private val inventoryClickTimeouts = Caffeine.newBuilder()
        .expireAfterWrite(
            plugin.configYml.getInt("refresh.inventory-click.timeout").toLong(),
            TimeUnit.MILLISECONDS
        )
        .build<UUID, Unit>()

    @EventHandler(priority = EventPriority.LOWEST)
    fun onItemPickup(event: EntityPickupItemEvent) {
        if (!plugin.configYml.getBool("refresh.pickup.enabled")) return
        if (plugin.configYml.getBool("refresh.pickup.require-meta")) {
            if (!event.item.itemStack.hasItemMeta()) {
                return
            }
        }

        event.entity.toDispatcher().refreshHolders()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Bukkit.getServer().onlinePlayers.forEach {
            it.toDispatcher().refreshHolders()
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryDrop(event: PlayerDropItemEvent) {
        if (!plugin.configYml.getBool("refresh.drop.enabled")) return
        if (plugin.configYml.getBool("refresh.drop.require-meta")) {
            if (!event.itemDrop.itemStack.hasItemMeta()) {
                return
            }
        }

        event.player.toDispatcher().refreshHolders()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChangeSlot(event: PlayerItemHeldEvent) {
        val player = event.player

        if (plugin.configYml.getBool("refresh.held.require-meta")) {
            val oldItem = player.inventory.getItem(event.previousSlot)
            val newItem = player.inventory.getItem(event.newSlot)
            if (((oldItem == null) || !oldItem.hasItemMeta()) && ((newItem == null) || !newItem.hasItemMeta())) {
                return
            }
        }

        val dispatcher = player.toDispatcher()

        plugin.scheduler.run {
            dispatcher.refreshHolders()
        }
    }

    @EventHandler
    fun onArmorChange(event: ArmorChangeEvent) {
        event.player.toDispatcher().refreshHolders()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        if (inventoryClickTimeouts.getIfPresent(player.uniqueId) != null) {
            return
        }

        inventoryClickTimeouts.put(player.uniqueId, Unit)

        player.toDispatcher().refreshHolders()
    }
}
