package com.willfp.libreforge.slot.impl

import com.willfp.libreforge.slot.CombinedSlotType
import com.willfp.libreforge.slot.SlotType
import com.willfp.libreforge.slot.SlotTypes

object SlotTypeAny : CombinedSlotType("any") {
    override val types: List<SlotType>
        get() = SlotTypes.values().filterNot { it is CombinedSlotType }.toList()

    override fun isOrContains(slotType: SlotType): Boolean {
        return true
    }
}
