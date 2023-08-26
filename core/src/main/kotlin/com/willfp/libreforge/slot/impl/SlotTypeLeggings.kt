package com.willfp.libreforge.slot.impl

import com.willfp.eco.core.items.isEmpty
import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeLeggings : SlotType("leggings") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (!player.inventory.leggings.isEmpty) {
            return false
        }

        player.inventory.leggings = item

        return true
    }

    override fun getItems(player: Player): List<ItemStack> {
        return player.inventory.leggings.toSingletonList()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return 37.toSingletonList()
    }
}
