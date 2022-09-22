package com.willfp.libreforge.filters

import com.willfp.libreforge.filters.filters.FilterAboveHealthPercent
import com.willfp.libreforge.filters.filters.FilterBlocks
import com.willfp.libreforge.filters.filters.FilterDamageCause
import com.willfp.libreforge.filters.filters.FilterEntityType
import com.willfp.libreforge.filters.filters.FilterFromSpawner
import com.willfp.libreforge.filters.filters.FilterFullyGrown
import com.willfp.libreforge.filters.filters.FilterItems
import com.willfp.libreforge.filters.filters.FilterOnMaxHealth
import com.willfp.libreforge.filters.filters.FilterOnlyBosses
import com.willfp.libreforge.filters.filters.FilterOnlyNonBosses
import com.willfp.libreforge.filters.filters.FilterPlayerPlaced
import com.willfp.libreforge.filters.filters.FilterProjectiles
import com.willfp.libreforge.filters.filters.FilterText
import com.willfp.libreforge.filters.filters.FilterTextContains

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
    val ON_MAX_HEALTH: FilterComponent = FilterOnMaxHealth()
    val ABOVE_HEALTH_PERCENT: FilterComponent = FilterAboveHealthPercent()
    val FULLY_GROWN: FilterComponent = FilterFullyGrown()
    val PLAYER_PLACED: FilterComponent = FilterPlayerPlaced()
    val TEXT: FilterComponent = FilterText()
    val TEXT_CONTAINS: FilterComponent = FilterTextContains()

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
