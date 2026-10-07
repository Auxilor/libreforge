package com.willfp.libreforge.slot

import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderProvider
import com.willfp.libreforge.HolderStates
import com.willfp.libreforge.ProvideContext
import com.willfp.libreforge.ScanningHolderProvider
import com.willfp.libreforge.ScopedHolderProvider
import com.willfp.libreforge.TypedHolderProvider
import com.willfp.libreforge.TypedProvidedHolder
import com.willfp.libreforge.get
import com.willfp.libreforge.ifType
import com.willfp.libreforge.isEcoEmpty
import com.willfp.libreforge.slot.impl.NumericSlotType
import org.bukkit.Material
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
     * As [findHolders], re-checking only the items that [scopes] match or whose type or amount
     * changed since the scan in [memory]. Null [scopes] re-check every item.
     */
    private fun findHolders(
        entity: LivingEntity,
        slot: SlotType,
        memory: ScanMemory,
        scopes: List<ItemScope>?
    ): List<TypedProvidedHolder<T>> {
        val items = slot.getItems(entity)
        val previous = memory.slots[slot]?.takeIf { it.types.size == items.size }
        val scan = previous ?: SlotScan(items.size).also { memory.slots[slot] = it }

        val holders = ArrayList<TypedProvidedHolder<T>>()

        items.forEachIndexed { index, item ->
            if (item.isEcoEmpty) {
                scan.types[index] = null
                scan.holders[index] = emptyList()
                return@forEachIndexed
            }

            val type = item.type
            val amount = item.amount

            val isUnchanged = scopes != null && previous != null
                    && scan.types[index] == type && scan.amounts[index] == amount
                    && scopes.none { it.matches(slot, item) }

            if (!isUnchanged) {
                scan.types[index] = type
                scan.amounts[index] = amount
                scan.holders[index] = this.find(item).filter { holder -> isValidInSlot(holder, slot) }
            }

            scan.holders[index].mapTo(holders) { holder -> SlotItemProvidedHolder(holder, item, slot) }
        }

        return holders
    }

    /**
     * What one slot held when last scanned: per item, its type, amount and holders.
     */
    private inner class SlotScan(size: Int) {
        val types = arrayOfNulls<Material>(size)
        val amounts = IntArray(size)
        val holders = Array<List<T>>(size) { emptyList() }
    }

    private inner class ScanMemory {
        val slots = HashMap<SlotType, SlotScan>()
    }

    /**
     * Convert this finder to a [HolderProvider].
     */
    fun toHolderProvider(): TypedHolderProvider<T> {
        return provider
    }

    internal inner class ItemHolderFinderProvider : TypedHolderProvider<T>, ScanningHolderProvider, ScopedHolderProvider {
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

        override fun scan(dispatcher: Dispatcher<*>): List<TypedProvidedHolder<T>> {
            val entity = dispatcher.get<LivingEntity>() ?: return emptyList()
            return slotsToScan(dispatcher).flatMap { slot -> findHolders(entity, slot) }
        }

        override fun provide(context: ProvideContext): List<TypedProvidedHolder<T>> {
            val entity = context.dispatcher.get<LivingEntity>() ?: return emptyList()
            val memory = context.memory.getOrPut { ScanMemory() }
            val scopes = context.scopes.allOf<ItemScope>()

            return slotsToScan(context.dispatcher).flatMap { slot -> findHolders(entity, slot, memory, scopes) }
        }

        private fun slotsToScan(dispatcher: Dispatcher<*>): Set<SlotType> {
            // Ordered, so duplicate holders keep their occurrence between scans.
            val slots = LinkedHashSet(SlotTypes.baseTypes)

            // Prevents double scanning of held item slot
            dispatcher.ifType<Player> {
                slots.remove(NumericSlotType(it.inventory.heldItemSlot))
            }

            // Only check for non-combined slot types
            return slots
        }
    }
}
