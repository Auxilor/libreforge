package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeArmor : SlotType("armor") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        for (slot in 36..39) {
            if (player.inventory.getItem(slot).isEcoEmpty) {
                player.inventory.setItem(slot, item)
                return true
            }
        }

        return false
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        return entity.equipment?.armorContents?.filterNotNull() ?: emptyList()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return (36..39).toList()
    }
}
