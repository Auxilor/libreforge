package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.cache.EcoCache
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
import java.time.Duration

object TriggerSmelt : Trigger("smelt") {
    private val playerCache = EcoCache.builder<Location, Player>()
        // Arbitrary long time
        .expireAfterWrite(Duration.ofMinutes(15))
        .build()

    override val description = "Fires when the player smelts an item in a furnace."

    override val categories = setOf("inventory")

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The location of the furnace.",
        TriggerParameter.ITEM to "The resulting item.",
        TriggerParameter.VALUE to "The number of items produced."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE
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

        val player = playerCache.get(event.block.location) ?: return
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
