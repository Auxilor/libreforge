package com.willfp.libreforge.slot.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeBoots : SlotType("boots") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (!player.inventory.boots.isEcoEmpty) {
            return false
        }

        player.inventory.boots = item

        return true
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        return entity.equipment?.boots.toSingletonList()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return 36.toSingletonList()
    }
}
