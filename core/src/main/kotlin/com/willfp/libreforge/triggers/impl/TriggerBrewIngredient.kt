package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.gui.player
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.BrewerInventory

object TriggerBrewIngredient : Trigger("brew_ingredient") {
    private val playerCache = mutableMapOf<Location, Player>()

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler
    fun handle(event: InventoryClickEvent) {
        val inventory = event.player.openInventory.topInventory as? BrewerInventory ?: return
        val player = event.player
        val location = inventory.location ?: return
        val oldContents = inventory.contents

        plugin.scheduler.runLater(2) {
            val newContents = inventory.contents

            if (!oldContents.contentEquals(newContents)) {
                playerCache[location] = player
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: BrewEvent) {
        val location = event.block.location

        val player = playerCache[location] ?: return

        val amount = (0..2).map { event.contents.getItem(it) }
            .count { !EmptyTestableItem().matches(it) }

        this.dispatch(
            PlayerDispatcher(player),
            TriggerData(
                player = player,
                item = event.contents.ingredient,
                value = amount.toDouble()
            )
        )

        playerCache.remove(location)
    }
}
