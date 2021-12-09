package com.willfp.libreforge.filters

import com.willfp.libreforge.filters.filters.*

object Filters {
    private val REGISTERED = mutableListOf<FilterComponent>()

    val ENTITY_TYPE: FilterComponent = FilterEntityType()
    val ONLY_BOSSES: FilterComponent = FilterOnlyBosses()
    val BLOCKS: FilterComponent = FilterBlocks()
    val DAMAGE_CAUSE: FilterComponent = FilterDamageCause()
    val ONLY_NON_BOSSES: FilterComponent = FilterOnlyNonBosses()

    /**
     * List of all registered filters.
     *
     * @return The filters.
     */
    fun values(): List<FilterComponent> {
        return REGISTERED.toList()
    }

    /**
     * Add new filter.
     *
     * @param filter The filter to add.
     */
    fun addNewFilter(filter: FilterComponent) {
        REGISTERED.add(filter)
    }
}
