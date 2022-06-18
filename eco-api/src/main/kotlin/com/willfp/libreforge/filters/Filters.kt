package com.willfp.libreforge.filters

import com.willfp.libreforge.filters.filters.FilterBlocks
import com.willfp.libreforge.filters.filters.FilterDamageCause
import com.willfp.libreforge.filters.filters.FilterEntityType
import com.willfp.libreforge.filters.filters.FilterFromSpawner
import com.willfp.libreforge.filters.filters.FilterItems
import com.willfp.libreforge.filters.filters.FilterOnlyBosses
import com.willfp.libreforge.filters.filters.FilterOnlyNonBosses
import com.willfp.libreforge.filters.filters.FilterProjectiles

@Suppress("UNUSED")
object Filters {
    private val REGISTERED = mutableListOf<FilterComponent>()

    val ENTITY_TYPE: FilterComponent = FilterEntityType()
    val ONLY_BOSSES: FilterComponent = FilterOnlyBosses()
    val BLOCKS: FilterComponent = FilterBlocks()
    val DAMAGE_CAUSE: FilterComponent = FilterDamageCause()
    val ONLY_NON_BOSSES: FilterComponent = FilterOnlyNonBosses()
    val ITEMS: FilterComponent = FilterItems()
    val PROJECTILES: FilterComponent = FilterProjectiles()
    val FROM_SPAWNER: FilterComponent = FilterFromSpawner()

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
