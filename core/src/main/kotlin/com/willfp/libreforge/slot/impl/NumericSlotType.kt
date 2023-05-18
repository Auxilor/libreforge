package com.willfp.libreforge.slot.impl

import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class NumericSlotType(
    private val slot: Int
) : SlotType(slot.toString()) {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        return if (EmptyTestableItem().matches(player.inventory.getItem(slot))) {
            player.inventory.setItem(slot, item)
            true
        } else {
            false
        }
    }
}
