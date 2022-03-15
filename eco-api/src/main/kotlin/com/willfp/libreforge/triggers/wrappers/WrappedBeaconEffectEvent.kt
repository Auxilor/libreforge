package com.willfp.libreforge.triggers.wrappers

import com.destroystokyo.paper.event.block.BeaconEffectEvent
import com.willfp.libreforge.triggers.WrappedCancellableEvent

class WrappedBeaconEffectEvent(private val event: BeaconEffectEvent): WrappedCancellableEvent<BeaconEffectEvent> {
    override var isCancelled: Boolean
        get() {
            return event.isCancelled
        }
        set(value) {
            event.isCancelled = value
        }
}
