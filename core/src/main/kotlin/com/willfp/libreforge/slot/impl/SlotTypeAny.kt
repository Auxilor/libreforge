package com.willfp.libreforge.slot.impl

import com.willfp.eco.core.drops.DropQueue
import com.willfp.libreforge.slot.CombinedSlotType
import com.willfp.libreforge.slot.SlotType
import com.willfp.libreforge.slot.SlotTypes
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeAny : CombinedSlotType("any") {
    override val types: List<SlotType>
        get() = SlotTypes.baseTypes

    override fun addToSlot(player: Player, item: ItemStack): Boolean {
        DropQueue(player)
            .addItem(item)
            .forceTelekinesis()
            .push()

        return true
    }

    override fun getItems(entity: LivingEntity): List<ItemStack> {
        return if (entity is Player) {
            entity.inventory.contents.filterNotNull()
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

    override fun isOrContains(slotType: SlotType): Boolean {
        return true
    }
}
