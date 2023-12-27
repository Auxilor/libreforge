package com.willfp.libreforge.slot.impl

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeAny : SlotType("any") {
    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        DropQueue(player)
            .addItem(item)
            .forceTelekinesis()
            .push()

        return true
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        return if (entity is Player) {
            entity.inventory.contents.toList().filterNotNull()
        } else {
            listOfNotNull(
                entity.equipment?.helmet,
                entity.equipment?.chestplate,
                entity.equipment?.leggings,
                entity.equipment?.boots,
                entity.equipment?.itemInMainHand,
                entity.equipment?.itemInOffHand
            )
        }
    }

    override fun getItemSlots(player: Player): List<Int> {
        return (0..45).toList()
    }
}
