package com.willfp.libreforge.slot

import com.willfp.libreforge.SignalScope
import org.bukkit.Material

/**
 * The items a signal touched: the [slots], and every item of the [types] wherever it is. Item
 * holder finders re-check only what this matches.
 */
class ItemScope private constructor(
    val slots: Set<SlotType>,
    val types: Set<Material>
) : SignalScope {
    /**
     * If the item of [type] in [slot] may have changed. [type] is null for an empty slot.
     */
    fun matches(slot: SlotType, type: Material?): Boolean =
        slot in slots || (type != null && type in types)

    companion object {
        /**
         * Touches the [slots].
         */
        @JvmStatic
        fun ofSlots(vararg slots: SlotType): ItemScope =
            of(slots.toList(), emptyList())

        /**
         * Touches every item of the [types], wherever it is.
         */
        @JvmStatic
        fun ofTypes(types: Collection<Material>): ItemScope =
            of(emptyList(), types)

        /**
         * Touches the [slots], and every item of the [types] wherever it is.
         */
        @JvmStatic
        fun of(slots: Collection<SlotType>, types: Collection<Material>): ItemScope =
            ItemScope(slots.toSet(), types.filterTo(HashSet()) { !it.isAir })
    }
}
