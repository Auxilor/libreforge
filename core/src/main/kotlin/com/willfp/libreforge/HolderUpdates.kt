package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.events.ArmorChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
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

    @EventHandler
    fun onItemPickup(event: EntityPickupItemEvent) {
        if (event.entity !is Player) {
            return
        }
        val player = event.entity as Player

        player.refreshHolders()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Bukkit.getServer().onlinePlayers.forEach {
            it.refreshHolders()
        }
    }

    @EventHandler
    fun onInventoryDrop(event: PlayerDropItemEvent) {
        event.player.refreshHolders()
    }

    @EventHandler
    fun onChangeSlot(event: PlayerItemHeldEvent) {
        event.player.refreshHolders()
        plugin.scheduler.run {
            event.player.refreshHolders()
        }
    }

    @EventHandler
    fun onArmorChange(event: ArmorChangeEvent) {
        event.player.refreshHolders()
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        if (inventoryClickTimeouts.getIfPresent(player.uniqueId) != null) {
            return
        }

        inventoryClickTimeouts.put(player.uniqueId, Unit)

        player.refreshHolders()
    }
}
