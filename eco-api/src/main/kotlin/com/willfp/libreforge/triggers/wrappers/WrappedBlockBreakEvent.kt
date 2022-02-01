package com.willfp.libreforge.triggers.wrappers

import com.willfp.libreforge.triggers.WrappedCancellableEvent
import org.bukkit.event.block.BlockBreakEvent

class WrappedBlockBreakEvent(private val event: BlockBreakEvent): WrappedCancellableEvent<BlockBreakEvent> {
    override var isCancelled: Boolean
        get() {
            return event.isCancelled
        }
        set(value) {
            event.isCancelled = value
        }
}