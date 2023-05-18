package com.willfp.libreforge.slot.impl

import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object HandSlotType : SlotType("hand") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        return if (EmptyTestableItem().matches(player.inventory.itemInMainHand)) {
            player.inventory.setItem(player.inventory.heldItemSlot, item)
            true
        } else {
            false
        }
    }
}
