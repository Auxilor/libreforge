package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerGroup
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object TriggerGroupCustom : TriggerGroup("custom") {
    private val registry = mutableMapOf<String, TriggerCustom>()

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

        fun dispatch(player: Player, data: TriggerData) {
            super.dispatch(
                player,
                data,
                null
            )
        }
    }
}
