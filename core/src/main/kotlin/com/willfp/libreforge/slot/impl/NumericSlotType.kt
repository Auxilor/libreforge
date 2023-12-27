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
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (!player.inventory.getItem(slot).isEcoEmpty) {
            return false
        }

        player.inventory.setItem(slot, item)
        return true
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        return if (entity is Player) {
            entity.inventory.getItem(slot).toSingletonList()
        } else {
            emptyList()
        }
    }

    override fun getItemSlots(player: Player): List<Int> {
        return slot.toSingletonList()
    }
}
