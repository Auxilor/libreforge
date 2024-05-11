package com.willfp.libreforge.slot

import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.slot.impl.NumericSlotType
import com.willfp.libreforge.slot.impl.SlotTypeAny
import com.willfp.libreforge.slot.impl.SlotTypeArmor
import com.willfp.libreforge.slot.impl.SlotTypeBoots
import com.willfp.libreforge.slot.impl.SlotTypeChestplate
import com.willfp.libreforge.slot.impl.SlotTypeHand
import com.willfp.libreforge.slot.impl.SlotTypeHands
import com.willfp.libreforge.slot.impl.SlotTypeHelmet
import com.willfp.libreforge.slot.impl.SlotTypeLeggings
import com.willfp.libreforge.slot.impl.SlotTypeMainhand
import com.willfp.libreforge.slot.impl.SlotTypeOffhand

object SlotTypes : Registry<SlotType>() {
    @Deprecated("Use SlotTypeMainhand instead", ReplaceWith("SlotTypeMainhand"), level = DeprecationLevel.ERROR)
    val mainHandSlot = SlotTypeMainhand

    override fun get(id: String): SlotType? {
        val existing = super.get(id)

        // Return existing slot type if it exists
        if (existing != null) {
            return existing
        }

        // Create new slot type if it doesn't exist
        return createNew(id)
            ?.apply { register(this) }
    }

    private fun createNew(id: String): SlotType? {
        if (id.contains(",")) {
            return CustomCombinedSlotType(
                id.split(",")
                    .mapNotNull { get(it.trim()) }
            )
        }

        if (id.toIntOrNull() != null) {
            return NumericSlotType(id.toInt())
        }

        return null
    }

    init {
        register(SlotTypeAny)
        register(SlotTypeArmor)
        register(SlotTypeBoots)
        register(SlotTypeChestplate)
        register(SlotTypeHand)
        register(SlotTypeMainhand)
        register(SlotTypeHands)
        register(SlotTypeHelmet)
        register(SlotTypeLeggings)
        register(SlotTypeOffhand)
    }
}
