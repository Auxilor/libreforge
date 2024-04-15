package com.willfp.libreforge.slot

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.abs

/**
 * A slot type that delegates to another slot type if possible.
 *
 * If the delegate is null, then it will add to any slot and get no items.
 */
internal class CombinedSlotTypes(
    val types: List<SlotType>
    // Use abs to prevent negative hash codes, which violate the ID pattern
) : SlotType("combined_" + abs(types.hashCode())) {
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

    override fun isOrContains(slotType: SlotType): Boolean {
        if (this == slotType) {
            return true
        }

        if (slotType is CombinedSlotTypes) {
            return slotType.types.all { isOrContains(it) }
        }
        
        return types.contains(slotType)
    }
}
