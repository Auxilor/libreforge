package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.slot.CombinedSlotType

object SlotTypeHands : CombinedSlotType("hands") {
    override val types = listOf(
        SlotTypeMainhand,
        SlotTypeOffhand
    )
}
