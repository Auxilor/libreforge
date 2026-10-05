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

        override fun scan(dispatcher: Dispatcher<*>): List<TypedProvidedHolder<T>> {
            val entity = dispatcher.get<LivingEntity>() ?: return emptyList()

            // Ordered, so duplicate holders keep their occurrence between scans.
            val slots = LinkedHashSet(SlotTypes.baseTypes)

            // Prevents double scanning of held item slot
            dispatcher.ifType<Player> {
                slots.remove(NumericSlotType(it.inventory.heldItemSlot))
            }

            // Only check for non-combined slot types
            return slots.flatMap { slot -> findHolders(entity, slot) }
        }
    }
}
