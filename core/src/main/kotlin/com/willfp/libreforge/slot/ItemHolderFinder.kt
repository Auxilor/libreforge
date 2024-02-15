package com.willfp.libreforge.slot

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderProvider
import com.willfp.libreforge.ItemProvidedHolder
import com.willfp.libreforge.TypedHolderProvider
import com.willfp.libreforge.TypedProvidedHolder
import com.willfp.libreforge.get
import com.willfp.libreforge.registerRefreshFunction
import com.willfp.libreforge.plugin
import org.bukkit.entity.LivingEntity
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
                .map { holder -> TypedItemProvidedHolder(holder, item) }
        }.flatten()

        return holders
    }

    /**
     * Convert this finder to a [HolderProvider].
     */
    fun toHolderProvider(): TypedHolderProvider<T> {
        return provider
    }

    private class TypedItemProvidedHolder<T : Holder>(
        override val holder: T,
        provider: ItemStack
    ) : ItemProvidedHolder(
        holder,
        provider
    ), TypedProvidedHolder<T>

    private inner class ItemHolderFinderProvider : TypedHolderProvider<T> {
        private val cache: Cache<UUID, List<TypedProvidedHolder<T>>> = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build()

        init {
            registerRefreshFunction {
                update(it)
            }
        }

        override fun provide(dispatcher: Dispatcher<*>): Collection<TypedProvidedHolder<T>> {
            val entity = dispatcher.get<LivingEntity>() ?: return emptyList()

            update(dispatcher)

            return cache.getIfPresent(dispatcher.uuid) ?: SlotTypes.values()
                .flatMap { slot -> findHolders(entity, slot) }
        }

        private fun update(dispatcher: Dispatcher<*>) {
            val entity = dispatcher.get<LivingEntity>() ?: return
            cache.put(dispatcher.uuid, SlotTypes.values().flatMap { slot -> findHolders(entity, slot) })
        }
    }
}
