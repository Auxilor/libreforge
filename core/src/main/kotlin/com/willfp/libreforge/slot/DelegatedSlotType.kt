package com.willfp.libreforge.slot

import com.willfp.libreforge.slot.impl.AnySlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

internal class DelegatedSlotType(
    private val delegate: SlotType?
) : SlotType("delegated") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (delegate == null || !delegate.addToSlot(player, item)) {
            AnySlotType.addToSlot(player, item)
        }

        return true
    }
}
