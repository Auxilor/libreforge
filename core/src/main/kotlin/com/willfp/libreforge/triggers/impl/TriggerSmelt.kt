package com.willfp.libreforge.triggers.impl

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.gui.player
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Location
import org.bukkit.block.Furnace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.FurnaceSmeltEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.FurnaceInventory
import java.util.concurrent.TimeUnit

object TriggerSmelt : Trigger("smelt") {
    private val playerCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build<Location, Player>()

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler
    fun handle(event: InventoryClickEvent) {
        val inventory = event.player.openInventory.topInventory as? FurnaceInventory ?: return
        val player = event.player
        val location = inventory.location ?: return
        val oldContents = inventory.contents

        plugin.scheduler.runLater(2) {
            val newContents = inventory.contents

            if (!oldContents.contentEquals(newContents)) {
                playerCache.put(location, player)
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: FurnaceSmeltEvent) {
        if (event.block.state !is Furnace) {
            return
        }

        val player = playerCache.getIfPresent(event.block.location) ?: return
        if (!player.isOnline) {
            return
        }

        playerCache.put(event.block.location, player) // Refresh cache

        val item = event.result

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = item,
                value = item.amount.toDouble(),
            )
        )
    }
}
