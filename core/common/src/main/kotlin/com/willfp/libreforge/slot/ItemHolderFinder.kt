package com.willfp.libreforge.slot

import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderProvider
import com.willfp.libreforge.HolderStates
import com.willfp.libreforge.ScanningHolderProvider
import com.willfp.libreforge.TypedHolderProvider
import com.willfp.libreforge.TypedProvidedHolder
import com.willfp.libreforge.get
import com.willfp.libreforge.ifType
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.impl.NumericSlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Finds holders on items for entities, allows for easy implementation of [HolderProvider].
 */
abstract class ItemHolderFinder<T : Holder> {
    /**
     * The [HolderProvider] for this finder.
     */
    private val provider: TypedHolderProvider<T> = ItemHolderFinderProvider()

    /**
     * Find holders on an [item].
     */
    abstract fun find(item: ItemStack): List<T>

    /**
     * Check if a given [holder] is valid for a given [slot].
     */
    abstract fun isValidInSlot(holder: T, slot: SlotType): Boolean

    /**
     * Find holders on an [entity] for a given [slot].
     */
    fun findHolders(entity: LivingEntity, slot: SlotType): List<TypedProvidedHolder<T>> {
        val items = slot.getItems(entity)

        val holders = items.flatMap { item ->
            if (item.isEcoEmpty) {
                return@flatMap emptyList()
            }

            this.find(item)
                .filter { holder -> isValidInSlot(holder, slot) }
                .map { holder -> SlotItemProvidedHolder(holder, item, slot) }
        }

        return holders
    }

    /**
     * As [findHolders], only searching items that changed since the scan in [memory].
     */
    private fun findHolders(entity: LivingEntity, slot: SlotType, memory: SlotScanMemory): List<TypedProvidedHolder<T>> {
        val items = slot.getItems(entity)
        val previous = memory[slot]

        val snapshots = ArrayList<ItemStack>(items.size)
        val found = ArrayList<List<Any>>(items.size)

        items.forEachIndexed { index, item ->
            val snapshot = previous?.items?.getOrNull(index)
            val holders = previous?.holders?.getOrNull(index)

            if (snapshot != null && holders != null && snapshot.amount == item.amount && snapshot.isSimilar(item)) {
                snapshots += snapshot
                found += holders
            } else {
                // A copy, as the item in the slot can change in place.
                snapshots += item.clone()
                found += if (item.isEcoEmpty) emptyList() else this.find(item).filter { holder -> isValidInSlot(holder, slot) }
            }
        }

        memory[slot] = SlotScanMemory.SlotScan(snapshots, found)

        return items.flatMapIndexed { index, item ->
            @Suppress("UNCHECKED_CAST")
            (found[index] as List<T>).map { holder -> SlotItemProvidedHolder(holder, item, slot) }
        }
    }

    /**
     * Convert this finder to a [HolderProvider].
     */
    fun toHolderProvider(): TypedHolderProvider<T> {
        return provider
    }

    internal inner class ItemHolderFinderProvider : TypedHolderProvider<T>, ScanningHolderProvider {
        val finderClass: Class<*>
            get() = this@ItemHolderFinder.javaClass

        override val id: String
            get() = finderClass.name

        override fun provide(dispatcher: Dispatcher<*>): Collection<TypedProvidedHolder<T>> {
            // Served from the dispatcher's state when it is tracked.
            @Suppress("UNCHECKED_CAST")
            return HolderStates.storedAnswer(dispatcher, this) as? Collection<TypedProvidedHolder<T>>
                ?: scan(dispatcher)
        }

        override fun scan(dispatcher: Dispatcher<*>): List<TypedProvidedHolder<T>> =
            scan(dispatcher, null)

        override fun scan(dispatcher: Dispatcher<*>, memory: SlotScanMemory?): List<TypedProvidedHolder<T>> {
            val entity = dispatcher.get<LivingEntity>() ?: return emptyList()

            // Ordered, so duplicate holders keep their occurrence between scans.
            val slots = LinkedHashSet(SlotTypes.baseTypes)

            // Prevents double scanning of held item slot
            dispatcher.ifType<Player> {
                slots.remove(NumericSlotType(it.inventory.heldItemSlot))
            }

            // Only check for non-combined slot types
            if (memory == null) {
                return slots.flatMap { slot -> findHolders(entity, slot) }
            }

            return slots.flatMap { slot -> findHolders(entity, slot, memory) }
        }
    }
}
