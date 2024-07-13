package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerGroup
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object TriggerGroupCustom : TriggerGroup("custom") {
    private val registry = mutableMapOf<String, TriggerCustom>()

    val knownTriggers: Set<String>
        get() = registry.keys

    override fun create(value: String): TriggerCustom {
        return registry.getOrPut(value) { TriggerCustom(value) }
    }

    class TriggerCustom(id: String) : Trigger("custom_$id") {
        override val parameters = setOf(
            TriggerParameter.PLAYER,
            TriggerParameter.LOCATION,
            TriggerParameter.VICTIM,
            TriggerParameter.BLOCK,
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
