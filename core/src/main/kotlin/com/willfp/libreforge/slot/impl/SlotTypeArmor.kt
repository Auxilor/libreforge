package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.CombinedSlotTypes
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeArmor : CombinedSlotTypes("armor") {
    override val types = listOf(
        SlotTypeHelmet,
        SlotTypeChestplate,
        SlotTypeLeggings,
        SlotTypeBoots
    )
}
