package com.willfp.libreforge.slot.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeMainhand : SlotType("mainhand") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        if (!player.inventory.itemInMainHand.isEcoEmpty) {
            return false
        }

        player.inventory.setItem(player.inventory.heldItemSlot, item)
        return true
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        return entity.equipment?.itemInMainHand.toSingletonList()
    }

    override fun getItemSlots(player: Player): List<Int> {
        return player.inventory.heldItemSlot.toSingletonList()
    }
}
