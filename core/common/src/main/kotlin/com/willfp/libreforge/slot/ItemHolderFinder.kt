package com.willfp.libreforge.slot

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderProvider
import com.willfp.libreforge.TypedHolderProvider
import com.willfp.libreforge.TypedProvidedHolder
import com.willfp.libreforge.get
import com.willfp.libreforge.ifType
import com.willfp.libreforge.isType
import com.willfp.libreforge.registerRefreshFunction
import com.willfp.libreforge.slot.impl.NumericSlotType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.TimeUnit

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

        val holders = items.map { item ->
            this.find(item)
                .filter { holder -> isValidInSlot(holder, slot) }
                .map { holder -> SlotItemProvidedHolder(holder, item, slot) }
        }.flatten()

        return holders
    }

    /**
     * Convert this finder to a [HolderProvider].
     */
    fun toHolderProvider(): TypedHolderProvider<T> {
        return provider
    }

    private inner class ItemHolderFinderProvider : TypedHolderProvider<T> {
        private val cache: Cache<UUID, List<TypedProvidedHolder<T>>> = Caffeine.newBuilder()
            .expireAfterWrite(500, TimeUnit.MILLISECONDS)
            .build()

        init {
            registerRefreshFunction {
                cache.invalidate(it.uuid)
            }
        }

        override fun provide(dispatcher: Dispatcher<*>): Collection<TypedProvidedHolder<T>> {
            return cache.get(dispatcher.uuid) {
                val entity = dispatcher.get<LivingEntity>() ?: return@get emptyList()

                val slots = SlotTypes.baseTypes.toMutableSet()

                // Prevents double scanning of held item slot
                dispatcher.ifType<Player> {
                    slots.remove(NumericSlotType(it.inventory.heldItemSlot))
                }

                // Only check for non-combined slot types
                slots.flatMap { slot -> findHolders(entity, slot) }
            }
        }
    }
}
