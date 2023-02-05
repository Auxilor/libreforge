package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerGroup
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player

class TriggerCustom(id: String) : Trigger(
    "custom_$id",
    TriggerParameter.values().toList()
) {
    fun invoke(player: Player, data: TriggerData, value: Double) {
        this.processTrigger(
            player,
            data,
            value
        )
    }

    companion object {
        private val intervals = mutableMapOf<String, TriggerCustom>()

        internal fun registerGroup() {
            Triggers.addNewTriggerGroup(
                object : TriggerGroup(
                    "custom"
                ) {
                    override fun create(value: String): Trigger {
                        return getWithID(value)
                    }
                }
            )
        }

        fun getWithID(id: String): TriggerCustom {
            if (intervals.containsKey(id)) {
                return intervals[id]!!
            }

            val trigger = TriggerCustom(id)
            intervals[id] = trigger

            return trigger
        }
    }
}
