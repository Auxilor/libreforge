package com.willfp.libreforge.slot.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class NumericSlotType(
    private val slot: Int
) : SlotType(slot.toString()) {
    private val slotList = slot.toSingletonList()

    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (slot !in 0 until player.inventory.size || !player.inventory.getItem(slot).isEcoEmpty) {
            return false
        }

        player.inventory.setItem(slot, item)
        return true
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        return if (entity is Player && slot in 0 until entity.inventory.size) {
            entity.inventory.getItem(slot).toSingletonList()
        } else {
            emptyList()
        }
    }

    override fun getItemSlots(player: Player): List<Int> {
        return slotList
    }
}
