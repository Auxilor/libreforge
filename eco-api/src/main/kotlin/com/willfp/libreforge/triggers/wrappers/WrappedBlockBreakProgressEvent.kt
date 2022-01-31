package com.willfp.libreforge.triggers.wrappers

import com.willfp.libreforge.triggers.WrappedCancellableEvent
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent

class WrappedBlockBreakProgressEvent(private val event: BlockDamageEvent): WrappedCancellableEvent<BlockDamageEvent> {
    override var isCancelled: Boolean
        get() {
            return event.isCancelled
        }
        set(value) {
            event.isCancelled = value
        }
}