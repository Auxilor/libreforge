package com.willfp.libreforge.slot

import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderProvider
import com.willfp.libreforge.ItemProvidedHolder
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.get
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

/**
 * Finds holders on items for entities, allows for easy implementation of [HolderProvider].
 */
abstract class ItemHolderFinder<T : Holder> {
    /**
     * Find holders on an [item].
     */
    abstract fun find(item: ItemStack): List<T>

    /**
     * Filter a list of [holders] to only those that are valid for a given [slot].
     */
    abstract fun filter(holders: List<T>, slot: SlotType): List<T>

    /**
     * Find holders on an [entity] for a given [slot].
     */
    fun findHolders(entity: LivingEntity, slot: SlotType): List<ProvidedHolder> {
        val items = slot.getItems(entity)

        val holders = items.map { item ->
            filter(this.find(item), slot).map { holder ->
                ItemProvidedHolder(holder, item)
            }
        }.flatten()

        return holders
    }

    /**
     * Convert this finder to a [HolderProvider].
     */
    fun toHolderProvider(): HolderProvider {
        return object : HolderProvider {
            override fun provide(dispatcher: Dispatcher<*>): Collection<ProvidedHolder> {
                val entity = dispatcher.get<LivingEntity>() ?: return emptyList()

                return SlotTypes.values()
                    .flatMap { slot -> findHolders(entity, slot) }
            }
        }
    }
}
