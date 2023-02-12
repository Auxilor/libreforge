package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.ItemStack

object TriggerCraft : Trigger("craft") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM
    )

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handle(event: CraftItemEvent) {
        if (event.result == Event.Result.DENY) {
            return
        }

        if (event.action == InventoryAction.NOTHING || event.inventory.result == null) {
            return
        }

        val player = event.whoClicked as? Player ?: return
        val item = event.recipe.result
        val cursor: ItemStack? = event.cursor
        var recipeRepetitions = 1
        val click = event.click

        if (click == ClickType.NUMBER_KEY) {
            // Hotbar crafting fails if cursor contains item, or hotbar destination is not empty.
            if (cursor != null && cursor.type != Material.AIR) {
                recipeRepetitions = 0
            } else if (player.inventory.getItem(event.hotbarButton) != null) {
                recipeRepetitions = 0
            }

        } else if (click == ClickType.DROP || click == ClickType.CONTROL_DROP) {
            // Drop crafting with Q fails if cursor is full
            if (cursor != null && cursor.type != Material.AIR) {
                recipeRepetitions = 0
            }

        } else if (click == ClickType.SWAP_OFFHAND) {
            // Can't craft into off-hand if off-hand is full.
            if (player.inventory.itemInOffHand.type != Material.AIR) {
                recipeRepetitions = 0
            }

        } else if (click == ClickType.SHIFT_RIGHT || click == ClickType.SHIFT_LEFT) {
            // This relies on the fact we can only craft as many as our smallest material stack.
            recipeRepetitions = Integer.MAX_VALUE
            for (istack: ItemStack? in event.inventory.matrix) {
                if (istack != null && istack.amount < recipeRepetitions) {
                    recipeRepetitions = istack.amount
                }
            }

            if (recipeRepetitions == Integer.MAX_VALUE) {
                recipeRepetitions = 0
            } else if (item.amount > 0) {
                // Determine how many of this stack we can fit in our inventory...
                var capacity = 0

                for (istack: ItemStack? in event.view.bottomInventory.storageContents) {
                    if (istack == null) {
                        capacity += item.maxStackSize
                    } else if (istack.isSimilar(item)) {
                        capacity += (item.maxStackSize - istack.amount).coerceAtLeast(0)
                    }
                }

                // If we can't fit everything, round up to the next full recipe amount.
                if (capacity < recipeRepetitions * item.amount) {
                    recipeRepetitions = ((capacity + item.amount - 1) / item.amount)
                }
            }
        }

        if (recipeRepetitions == 0) {
            return
        }

        this.dispatch(
            player,
            TriggerData(
                player = player,
                item = item,
                value = recipeRepetitions.toDouble()
            )
        )
    }
}
