package com.willfp.libreforge.slot.impl

import com.willfp.eco.core.items.isEmpty
import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// Pass in any ID, so you can have both mainhand and hand as valid IDs
class SlotTypeMainhand(id: String) : SlotType(id) {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (!player.inventory.itemInMainHand.isEmpty) {
            return false
        }

        player.inventory.setItem(player.inventory.heldItemSlot, item)
        return true
    }

    override fun getItems(player: Player): List<ItemStack> {
        return player.inventory.itemInMainHand.toSingletonList()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return player.inventory.heldItemSlot.toSingletonList()
    }
}
