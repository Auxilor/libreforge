package com.willfp.libreforge.triggers.wrappers

import com.willfp.libreforge.triggers.WrappedEvent
import org.bukkit.event.player.PlayerExpChangeEvent

class WrappedXpEvent(
    private val event: PlayerExpChangeEvent
) : WrappedEvent<PlayerExpChangeEvent> {
    var amount: Int
        get() = event.amount
        set(value) {
            event.amount = value
        }
}
