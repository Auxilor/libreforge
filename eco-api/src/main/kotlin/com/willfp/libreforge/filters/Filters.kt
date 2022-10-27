package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.filters.FilterAboveHealthPercent
import com.willfp.libreforge.filters.filters.FilterBlocks
import com.willfp.libreforge.filters.filters.FilterDamageCause
import com.willfp.libreforge.filters.filters.FilterEntityType
import com.willfp.libreforge.filters.filters.FilterFromSpawner
import com.willfp.libreforge.filters.filters.FilterFullyCharged
import com.willfp.libreforge.filters.filters.FilterFullyGrown
import com.willfp.libreforge.filters.filters.FilterIsBehindVictim
import com.willfp.libreforge.filters.filters.FilterItems
import com.willfp.libreforge.filters.filters.FilterOnMaxHealth
import com.willfp.libreforge.filters.filters.FilterOnlyBosses
import com.willfp.libreforge.filters.filters.FilterOnlyNonBosses
import com.willfp.libreforge.filters.filters.FilterPlayerPlaced
import com.willfp.libreforge.filters.filters.FilterProjectiles
import com.willfp.libreforge.filters.filters.FilterText
import com.willfp.libreforge.filters.filters.FilterTextContains
import com.willfp.libreforge.triggers.TriggerData

@Suppress("UNUSED")
object Filters {
    private val REGISTERED = mutableListOf<Filter>()

    val ENTITY_TYPE: Filter = FilterEntityType
    val ONLY_BOSSES: Filter = FilterOnlyBosses
    val BLOCKS: Filter = FilterBlocks
    val DAMAGE_CAUSE: Filter = FilterDamageCause
    val ONLY_NON_BOSSES: Filter = FilterOnlyNonBosses
    val ITEMS: Filter = FilterItems
    val PROJECTILES: Filter = FilterProjectiles
    val FROM_SPAWNER: Filter = FilterFromSpawner
    val ON_MAX_HEALTH: Filter = FilterOnMaxHealth
    val ABOVE_HEALTH_PERCENT: Filter = FilterAboveHealthPercent
    val FULLY_GROWN: Filter = FilterFullyGrown
    val PLAYER_PLACED: Filter = FilterPlayerPlaced
    val TEXT: Filter = FilterText
    val TEXT_CONTAINS: Filter = FilterTextContains
    val IS_BEHIND_VICTIM: Filter = FilterIsBehindVictim
    val FULLY_CHARGED: Filter = FilterFullyCharged

    /**
     * List of all registered filters.
     *
     * @return The filters.
     */
    fun values(): List<Filter> {
        return REGISTERED.toList()
    }

    /**
     * Add new filter.
     *
     * @param filter The filter to add.
     */
    fun addNewFilter(filter: Filter) {
        REGISTERED.add(filter)
    }

    /**
     * If the data passes the filters.
     *
     * @param data The data.
     * @param config The filters.
     * @return If passes.
     */
    fun passes(data: TriggerData, config: Config): Boolean {
        val testResults = mutableListOf<Boolean>()

        for (filter in values()) {
            testResults.add(filter.passes(data, config))
        }

        return testResults.isEmpty() || testResults.stream().allMatch { it }
    }
}
