package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.slot.CombinedSlotType

object SlotTypeArmor : CombinedSlotType("armor") {
    override val types = listOf(
        SlotTypeHelmet,
        SlotTypeChestplate,
        SlotTypeLeggings,
        SlotTypeBoots
    )
}
