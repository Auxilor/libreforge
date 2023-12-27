package com.willfp.libreforge.slot

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * A slot type that delegates to another slot type if possible.
 *
 * If the delegate is null, then it will add to any slot and get no items.
 */
internal class CombinedSlotTypes(
    private val types: List<SlotType>
) : SlotType("combined_" + types.hashCode()) {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        return types.any { it.addToSlot(player, item) }
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        val items = mutableSetOf<ItemStack>()

        return items.apply {
            types.flatMapTo(this) { it.getItems(entity) }
        }.toList()
    }

    override fun getItemSlots(player: Player): List<Int> {
        val items = mutableSetOf<Int>()

        return items.apply {
            types.flatMapTo(this) { it.getItemSlots(player) }
        }.toList()
    }
}
