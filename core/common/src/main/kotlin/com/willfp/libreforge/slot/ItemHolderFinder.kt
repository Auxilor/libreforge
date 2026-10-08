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
     * The scan of [slot] from [read], re-finding only the items that [scopes] match or whose type
     * changed since [scan]. Null [scopes] re-find every item. Keeps the provided holders of [scan]
     * when the holders found are the same.
     */
    private fun rescan(slot: SlotType, read: ReadSlot, scan: SlotScan?, scopes: List<ItemScope>?): SlotScan {
        val size = read.items.size
        val previous = scan?.takeIf { it.types.size == size }
        val reusable = previous?.takeIf { scopes != null }
        val holders = Array<List<T>>(size) { emptyList() }
        var isSame = previous != null

        for (index in 0 until size) {
            val type = read.types[index]

            holders[index] = if (type == null) {
                emptyList()
            } else if (reusable != null && reusable.types[index] == type && scopes!!.none { it.matches(slot, type) }) {
                reusable.holders[index]
            } else {
                this.find(read.items[index]).filter { holder -> isValidInSlot(holder, slot) }
            }

            if (isSame && !isSameHolders(previous!!.holders[index], holders[index])) {
                isSame = false
            }
        }

        val provided = if (isSame) {
            previous!!.provided
        } else {
            val list = ArrayList<TypedProvidedHolder<T>>()
            holders.forEachIndexed { index, found ->
                found.mapTo(list) { holder -> SlotItemProvidedHolder(holder, read.items[index], slot) }
            }
            list
        }

        return SlotScan(read.types.copyOf(), holders, provided)
    }

    private fun isSameHolders(old: List<T>, new: List<T>): Boolean {
        if (old.size != new.size) {
            return false
        }

        return old.indices.all { old[it] === new[it] }
    }

    /**
     * What one slot held when last scanned: per item, its type (null if empty) and holders, and
     * the holders as provided.
     */
    private inner class SlotScan(
        val types: Array<Material?>,
        val holders: Array<List<T>>,
        val provided: List<TypedProvidedHolder<T>>
    )

    private inner class ScanMemory {
        val slots = HashMap<SlotType, SlotScan>()
        var order: List<SlotType> = emptyList()
        var answer: List<TypedProvidedHolder<T>>? = null
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
            val reads = context.pass.getOrPut(SlotReads::class.java) { SlotReads(entity) }
            val scopes = context.scopes.allOf<ItemScope>()
            val previous = memory.answer
            val order = slotsToScan(context.dispatcher).toList()

            // Only a type scope can match an item in any slot; slot scopes name every slot they touch.
            val walkAll = previous == null || scopes == null || scopes.any { it.types.isNotEmpty() }
            var isChanged = previous == null || order != memory.order

            for (slot in order) {
                val scan = memory.slots[slot]

                if (scan != null && !walkAll && scopes!!.none { slot in it.slots }) {
                    continue
                }

                val rescanned = rescan(slot, reads.read(slot), scan, scopes)
                memory.slots[slot] = rescanned

                if (rescanned.provided !== scan?.provided) {
                    isChanged = true
                }
            }

            if (!isChanged && previous != null) {
                return previous
            }

            return ArrayList<TypedProvidedHolder<T>>().also { list ->
                order.forEach { slot -> memory.slots[slot]?.let { list.addAll(it.provided) } }
                memory.answer = list
                memory.order = order
            }
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
