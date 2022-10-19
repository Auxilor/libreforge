package com.willfp.libreforge.triggers

import com.willfp.libreforge.BlankHolder
import org.bukkit.entity.Player

internal object BlankTrigger : Trigger(
    "blank",
    TriggerParameter.values().toList()
) {
    fun createInvocation(player: Player, data: TriggerData): InvocationData {
        return InvocationData(
            player,
            data,
            BlankHolder,
            this,
            null,
            1.0
        )
    }
}
