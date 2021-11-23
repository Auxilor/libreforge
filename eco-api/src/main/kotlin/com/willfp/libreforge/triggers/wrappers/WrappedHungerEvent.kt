package com.willfp.libreforge.triggers.wrappers

import com.willfp.libreforge.triggers.WrappedCancellableEvent
import org.bukkit.event.entity.FoodLevelChangeEvent

class WrappedHungerEvent(
    private val event: FoodLevelChangeEvent
) : WrappedCancellableEvent<FoodLevelChangeEvent> {
    var amount: Int
        get() {
            return event.entity.foodLevel - event.foodLevel
        }
        set(value) {
            event.foodLevel = event.entity.foodLevel - value
        }

    override var isCancelled: Boolean
        get() {
            return event.isCancelled
        }
        set(value) {
            event.isCancelled = value
        }
}
