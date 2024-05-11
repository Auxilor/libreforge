package com.willfp.libreforge.slot

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * A slot type that functions as a collection of other slot types.
 */
abstract class CombinedSlotType(
    id: String
) : SlotType(id) {
    /**
     * The types to combine.
     */
    abstract val types: List<SlotType>

    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        return types.any { it.addToSlot(player, item) }
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        val items = mutableListOf<ItemStack>()

        return items.apply {
            types.flatMapTo(this) { it.getItems(entity) }
        }
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

        if (slotType is CombinedSlotType) {
            return slotType.types.all { isOrContains(it) }
        }
        
        return types.contains(slotType)
    }
}
