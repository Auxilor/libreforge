package com.willfp.libreforge.slot

import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.slot.impl.SlotTypeAny
import com.willfp.libreforge.slot.impl.SlotTypeArmor
import com.willfp.libreforge.slot.impl.SlotTypeBoots
import com.willfp.libreforge.slot.impl.SlotTypeChestplate
import com.willfp.libreforge.slot.impl.SlotTypeMainhand
import com.willfp.libreforge.slot.impl.SlotTypeHands
import com.willfp.libreforge.slot.impl.SlotTypeHelmet
import com.willfp.libreforge.slot.impl.SlotTypeLeggings
import com.willfp.libreforge.slot.impl.NumericSlotType
import com.willfp.libreforge.slot.impl.SlotTypeOffhand

object SlotTypes : Registry<SlotType>() {
    override fun get(id: String): SlotType {
        if (id.toIntOrNull() != null) {
            return DelegatedSlotType(NumericSlotType(id.toInt()))
        }

        return DelegatedSlotType(super.get(id))
    }

    init {
        register(SlotTypeAny)
        register(SlotTypeArmor)
        register(SlotTypeBoots)
        register(SlotTypeChestplate)
        register(SlotTypeMainhand("hand"))
        register(SlotTypeMainhand("mainhand"))
        register(SlotTypeHands)
        register(SlotTypeHelmet)
        register(SlotTypeLeggings)
        register(SlotTypeOffhand)
    }
}
