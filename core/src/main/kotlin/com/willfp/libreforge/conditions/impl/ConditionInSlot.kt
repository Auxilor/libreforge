package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.slot.SlotItemProvidedHolder
import com.willfp.libreforge.slot.SlotType
import com.willfp.libreforge.slot.SlotTypes

object ConditionInSlot : Condition<List<SlotType>>("in_slot") {
    override val arguments = arguments {
        require(listOf("slot", "slots"), "You must specify the slot(s)!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: List<SlotType>
    ): Boolean {
        if (holder !is SlotItemProvidedHolder<*>) {
            return false
        }

        return compileData.any { holder.slotType.isOrContains(it) }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<SlotType> {
        return config.getStrings("slots", "slot")
            .mapNotNull { SlotTypes[it] }
    }
}
