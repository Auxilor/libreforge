package com.willfp.libreforge.slot.impl

import com.willfp.eco.core.items.isEmpty
import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeArmor : SlotType("armor") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        for (slot in 36..39) {
            if (player.inventory.getItem(slot).isEmpty) {
                player.inventory.setItem(slot, item)
                return true
            }
        }

        return false
    }

    override fun getItems(player: Player): List<ItemStack> {
        return player.inventory.armorContents.filterNotNull()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return (36..39).toList()
    }
}
