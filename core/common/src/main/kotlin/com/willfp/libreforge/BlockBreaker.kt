package com.willfp.libreforge

import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.block.Block

/**
 * A [Dispatcher] that breaks blocks itself.
 *
 * Block-breaking effects go through the player by default, which leaves the
 * drops on the floor and gives the dispatcher no say over how the blocks are
 * removed. A dispatcher that owns the blocks it mines - a minion, a machine -
 * implements this to take the drops and the removal for itself.
 *
 * A dispatcher that does not implement this is broken through the player,
 * exactly as before.
 */
interface BlockBreaker {
    /**
     * Break [blocks] on behalf of this dispatcher.
     *
     * @param data The trigger the breaking effect is running for, which carries
     *             the drop event the drops belong in, where one exists.
     */
    fun breakBlocks(blocks: Collection<Block>, data: TriggerData)
}
