package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerGroup
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerGroupCustom : TriggerGroup("custom") {
    private val registry = mutableMapOf<String, TriggerCustom>()

    val knownTriggers: Set<String>
        get() = registry.keys

    override fun create(value: String): TriggerCustom {
        return registry.getOrPut(value) { TriggerCustom(value) }
    }

    class TriggerCustom(id: String) : Trigger("custom_$id") {
        override val description = "A custom trigger dispatched manually by other effects or plugins."

        override val categories = setOf("meta")

        override val parameterDescriptions = mapOf(
            TriggerParameter.VICTIM to "The victim entity, if provided by the dispatcher.",
            TriggerParameter.BLOCK to "The block, if provided by the dispatcher.",
            TriggerParameter.LOCATION to "The location, if provided by the dispatcher.",
            TriggerParameter.VELOCITY to "The velocity, if provided by the dispatcher.",
            TriggerParameter.ITEM to "The item, if provided by the dispatcher."
        )

        override val parameters = setOf(
            TriggerParameter.PLAYER,
            TriggerParameter.VICTIM,
            TriggerParameter.BLOCK,
            TriggerParameter.LOCATION,
            TriggerParameter.VELOCITY,
            TriggerParameter.ITEM
        )

        fun dispatch(dispatcher: Dispatcher<*>, data: TriggerData) {
            super.dispatch(
                dispatcher,
                data,
                null
            )
        }
    }
}
