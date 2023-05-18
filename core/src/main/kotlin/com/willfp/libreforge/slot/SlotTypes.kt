package com.willfp.libreforge.slot

import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.slot.impl.AnySlotType
import com.willfp.libreforge.slot.impl.HandSlotType
import com.willfp.libreforge.slot.impl.NumericSlotType
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object SlotTypes : Registry<SlotType>() {
    fun getByID(id: String?): SlotType {
        if (id == null) {
            return AnySlotType
        }

        return get(id)
    }

    override fun get(id: String): SlotType {
        if (id.toIntOrNull() != null) {
            return DelegatedSlotType(NumericSlotType(id.toInt()))
        }

        return DelegatedSlotType(super.get(id))
    }

    /**
     * Get a predicate requiring certain trigger parameters.
     */
    fun withParameters(parameters: Set<TriggerParameter>): (Trigger) -> Boolean {
        return {
            it.parameters.flatMap { param -> param.inheritsFrom.toList().plusElement(param) }.containsAll(parameters)
        }
    }

    init {
        register(AnySlotType)
        register(HandSlotType)
    }
}
