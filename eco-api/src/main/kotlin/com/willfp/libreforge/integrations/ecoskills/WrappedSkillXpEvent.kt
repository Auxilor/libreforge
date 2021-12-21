package com.willfp.libreforge.integrations.ecoskills

import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.libreforge.triggers.WrappedEvent

class WrappedSkillXpEvent(
    private val event: PlayerSkillExpGainEvent
) : WrappedEvent<PlayerSkillExpGainEvent> {
    var amount: Double
        get() = event.amount
        set(value) {
            event.amount = value
        }
}
