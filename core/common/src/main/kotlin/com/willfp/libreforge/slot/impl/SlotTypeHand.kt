package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.slot.CombinedSlotType

object SlotTypeHand : CombinedSlotType("hand") {
    override val types = listOf(
        SlotTypeMainhand
    )
}
