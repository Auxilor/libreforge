package com.willfp.libreforge.slot.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeLeggings : SlotType("leggings") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (!player.inventory.leggings.isEcoEmpty) {
            return false
        }

        player.inventory.leggings = item

        return true
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        return entity.equipment?.leggings.toSingletonList()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return 37.toSingletonList()
    }
}
