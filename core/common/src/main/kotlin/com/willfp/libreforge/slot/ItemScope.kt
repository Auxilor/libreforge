package com.willfp.libreforge.slot

import com.willfp.libreforge.SignalScope
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * The items a signal touched. Item holder finders re-check only the slots this matches, plus any
 * slot whose item type or amount changed since it was last checked.
 */
class ItemScope(
    private val predicate: (SlotType, ItemStack) -> Boolean
) : SignalScope {
    /**
     * If the [item] in [slot] may have changed.
     */
    fun matches(slot: SlotType, item: ItemStack): Boolean =
        predicate(slot, item)

    companion object {
        /**
         * Touches the [slots].
         */
        @JvmStatic
        fun ofSlots(vararg slots: SlotType): ItemScope {
            val set = slots.toSet()
            return ItemScope { slot, _ -> slot in set }
        }

        /**
         * Touches every item of the [types], wherever it is.
         */
        @JvmStatic
        fun ofTypes(types: Collection<Material>): ItemScope {
            val set = types.filterTo(HashSet()) { !it.isAir }
            return ItemScope { _, item -> item.type in set }
        }

        /**
         * Touches the [slots], and every item of the [types] wherever it is.
         */
        @JvmStatic
        fun of(slots: Collection<SlotType>, types: Collection<Material>): ItemScope {
            val slotSet = slots.toSet()
            val typeSet = types.filterTo(HashSet()) { !it.isAir }
            return ItemScope { slot, item -> slot in slotSet || item.type in typeSet }
        }
    }
}
