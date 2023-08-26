package com.willfp.libreforge.slot

import com.willfp.libreforge.slot.impl.SlotTypeAny
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * A slot type that delegates to another slot type if possible.
 *
 * If the delegate is null, then it will add to any slot and get no items.
 */
internal class DelegatedSlotType(
    private val delegate: SlotType?
) : SlotType("delegated") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (delegate == null || !delegate.addToSlot(player, item)) {
            SlotTypeAny.addToSlot(player, item)
        }

        return true
    }

    override fun getItems(player: Player): List<ItemStack> {
        return delegate?.getItems(player) ?: emptyList()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return delegate?.getItemSlots(player) ?: emptyList()
    }
}
