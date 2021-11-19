package com.willfp.libreforge.filters

import com.google.common.collect.HashBiMap
import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.filters.filters.FilterBlock
import com.willfp.libreforge.filters.filters.FilterEmpty
import com.willfp.libreforge.filters.filters.FilterLivingEntity

typealias FilterProvider = (config: JSONConfig) -> Filter

object Filters {
    private val BY_ID = HashBiMap.create<String, FilterProvider>()

    val BLOCK: FilterProvider = { FilterBlock(it) }
    val LIVING_ENTITY: FilterProvider = { FilterLivingEntity(it) }

    /**
     * Create a filter by id and config.
     *
     * @param id The id.
     * @param config The config.
     */
    fun createById(id: String, config: JSONConfig): Filter {
        return BY_ID[id]?.invoke(config) ?: FilterEmpty()
    }

    /**
     * Add new filter.
     *
     * @param id The id of the filter.
     * @param provider The filter provider.
     */
    fun addNewFilter(id: String, provider: FilterProvider) {
        BY_ID.remove(id)
        BY_ID[id] = provider
    }
}
