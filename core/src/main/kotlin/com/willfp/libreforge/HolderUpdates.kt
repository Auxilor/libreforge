package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.events.ArmorChangeEvent
import com.willfp.ecoenchants.target.EnchantmentTargets.isEnchantable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent


@Suppress("unused", "UNUSED_PARAMETER")
class ItemRefreshListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler
    fun onItemPickup(event: EntityPickupItemEvent) {
        if (event.entity !is Player) {
            return
        }
        val player = event.entity as Player

        if (!event.item.itemStack.isEnchantable) {
            return
        }

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
        if (!event.itemDrop.itemStack.isEnchantable) {
            return
        }

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
        player.refreshHolders()
    }
}
