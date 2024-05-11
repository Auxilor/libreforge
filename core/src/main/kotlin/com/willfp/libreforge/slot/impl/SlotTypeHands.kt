package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.slot.CombinedSlotTypes
import com.willfp.libreforge.slot.SlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SlotTypeHands : CombinedSlotTypes("hands") {
    override val types = listOf(
        SlotTypeMainhand,
        SlotTypeOffhand
    )
}
