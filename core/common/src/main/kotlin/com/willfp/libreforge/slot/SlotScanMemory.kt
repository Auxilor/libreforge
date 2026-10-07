package com.willfp.libreforge.slot

import com.willfp.libreforge.HolderStates
import org.bukkit.inventory.ItemStack

/**
 * What one dispatcher's slots held when they were last scanned, so unchanged items are not searched
 * for holders again. Belongs to a holder state, so is only touched on its thread.
 */
internal class SlotScanMemory {
    class SlotScan(
        val items: List<ItemStack>,
        val holders: List<List<Any>>
    )

    private var generation = HolderStates.resetGeneration

    private val slots = HashMap<SlotType, SlotScan>()

    /**
     * The last scan of [slot], or null if there is none from the current configuration.
     */
    operator fun get(slot: SlotType): SlotScan? {
        val current = HolderStates.resetGeneration
        if (generation != current) {
            generation = current
            slots.clear()
        }

        return slots[slot]
    }

    operator fun set(slot: SlotType, scan: SlotScan) {
        slots[slot] = scan
    }
}
