package com.willfp.libreforge.slot

import com.willfp.libreforge.isEcoEmpty
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

/**
 * One slot's items as read in a pass, with each item's type and stack handle, null where empty.
 */
internal class ReadSlot(val items: List<ItemStack>) {
    val types: Array<Material?> = Array(items.size) { index ->
        items[index].takeUnless { it.isEcoEmpty }?.type
    }

    val handles: Array<Any?> = Array(items.size) { index ->
        types[index]?.let { ItemHandles.handleOf(items[index]) }
    }
}

/**
 * The slots of an [entity] read so far in one pass, shared by every item holder finder asked in it.
 */
internal class SlotReads(private val entity: LivingEntity) {
    private val slots = HashMap<SlotType, ReadSlot>()

    fun read(slot: SlotType): ReadSlot = slots.getOrPut(slot) {
        ReadSlot(slot.getItems(entity))
    }
}
