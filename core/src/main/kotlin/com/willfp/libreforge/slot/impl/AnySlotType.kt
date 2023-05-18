package com.willfp.libreforge.slot.impl

import com.willfp.eco.core.drops.DropQueue
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object AnySlotType : SlotType("any") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        DropQueue(player)
            .addItem(item)
            .forceTelekinesis()
            .push()

        return true
    }
}
