package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.slot.CombinedSlotTypes

object SlotTypeHand : CombinedSlotTypes("hand") {
    override val types = listOf(
        SlotTypeMainhand
    )
}
