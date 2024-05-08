package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
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
import org.bukkit.inventory.PlayerInventory

object TriggerCraft : Trigger("craft") {

    private const val CRAFTING_FAILED = 0

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
        val cursor = event.cursor
        val recipeRepetitions = when (event.click) {
            ClickType.NUMBER_KEY -> handleNumberKeyCompletion(cursor, player, event)
            ClickType.DROP, ClickType.CONTROL_DROP -> handleDropCompletion(cursor)
            ClickType.SWAP_OFFHAND -> handleSwapOffhandCompletion(player)
            ClickType.SHIFT_RIGHT, ClickType.SHIFT_LEFT -> handleShiftClickCompletion(event, item)
            else -> 1
        }
        if (recipeRepetitions == CRAFTING_FAILED) {
            return
        }
        val totalItemsCrafted = item.amount * recipeRepetitions
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                item = item,
                value = totalItemsCrafted.toDouble()
            )
        )
    }

    private fun handleShiftClickCompletion(event: CraftItemEvent, result: ItemStack): Int {
        val inventoryContent = event.view.topInventory.storageContents.toList().filterNot { item ->
            item == null || item.type.isAir
        }.drop(1)

        val playerInventory = event.view.bottomInventory as PlayerInventory
        val contents = playerInventory.storageContents.toList()
        val totalPossibleSlotsForItems = contents.sumOf { item ->
            val slotIsBlank = item == null || item.type.isAir
            if (slotIsBlank) {
                return@sumOf result.maxStackSize
            }
            val itemIsResult = item!!.isSimilar(result)
            if (itemIsResult) {
                return@sumOf result.maxStackSize - item.amount
            }
            0
        }

        if (totalPossibleSlotsForItems == 0) {
            return CRAFTING_FAILED
        }

        val totalCraftableItems = inventoryContent.minOf { it!!.amount }
        return if (totalCraftableItems <= totalPossibleSlotsForItems) {
            totalCraftableItems
        } else {
            totalPossibleSlotsForItems / result.amount
        }
    }

    private fun handleSwapOffhandCompletion(player: Player): Int {
        val playerOffhandIsNotEmpty = player.inventory.itemInOffHand.type != Material.AIR
        // Can't craft into off-hand if off-hand is full.
        return if (playerOffhandIsNotEmpty) {
            CRAFTING_FAILED
        } else {
            1
        }
    }

    private fun handleDropCompletion(cursor: ItemStack?): Int {
        // Drop crafting with Q fails if cursor is full
        val curseIsNotEmpty = cursor != null && cursor.type != Material.AIR
        return if (curseIsNotEmpty) {
            CRAFTING_FAILED
        } else {
            1
        }
    }

    private fun handleNumberKeyCompletion(cursor: ItemStack?, player: Player, event: CraftItemEvent): Int {
        val cursorIsNotEmpty = cursor != null && cursor.type != Material.AIR
        val playerHasItemInSlot = player.inventory.getItem(event.hotbarButton) != null
        // Hotbar crafting fails if cursor contains item, or hotbar destination is not empty.
        return if (cursorIsNotEmpty || playerHasItemInSlot) {
            CRAFTING_FAILED
        } else {
            1
        }
    }

}