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

    lateinit var baseTypes: List<SlotType>
        private set

    override fun get(id: String): SlotType? {
        val existing = super.get(id)

        // Return existing slot type if it exists
        if (existing != null) {
            return existing
        }

        // Prevent registering numeric slot types that correspond to armor slots
        val idInt = id.toIntOrNull()
        if (idInt != null) {
            when (idInt) {
                40 -> return SlotTypeOffhand
                36 -> return SlotTypeBoots
                37 -> return SlotTypeLeggings
                38 -> return SlotTypeChestplate
                39 -> return SlotTypeHelmet
            }
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

        val idInt = id.toIntOrNull()
        if (idInt != null) {
            return NumericSlotType(idInt)
        }

        return null
    }

    override fun onRegister(element: SlotType) {
        baseTypes = values().filter { it !is CombinedSlotType }
    }

    override fun onRemove(element: SlotType) {
        baseTypes = values().filter { it !is CombinedSlotType }
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

        // Register all numeric slots
        (0..45).forEach {
            get(it.toString())
        }
    }
}
