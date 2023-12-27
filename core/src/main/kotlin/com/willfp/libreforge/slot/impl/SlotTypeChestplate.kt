package com.willfp.libreforge.slot.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeChestplate : SlotType("chestplate") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (!player.inventory.chestplate.isEcoEmpty) {
            return false
        }

        player.inventory.chestplate = item

        return true
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        return entity.equipment?.chestplate.toSingletonList()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return 38.toSingletonList()
    }
}
