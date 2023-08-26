package com.willfp.libreforge.slot.impl

import com.willfp.eco.core.drops.DropQueue
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeAny : SlotType("any") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        DropQueue(player)
            .addItem(item)
            .forceTelekinesis()
            .push()

        return true
    }

    override fun getItems(player: Player): List<ItemStack> {
        return player.inventory.contents.toList().filterNotNull()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return (0..45).toList()
    }
}
