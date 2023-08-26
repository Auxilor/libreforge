package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeHands : SlotType("hands") {
    private val mainhand = SlotTypeMainhand("<internal>")

    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (!mainhand.addToSlot(player, item)) {
            return SlotTypeOffhand.addToSlot(player, item)
        }

        return true
    }

    override fun getItems(player: Player): List<ItemStack> {
        return SlotTypeOffhand.getItems(player) + mainhand.getItems(player)
    }

    override fun getItemSlots(player: Player): List<Int> {
        return SlotTypeOffhand.getItemSlots(player) + mainhand.getItemSlots(player)
    }
}
