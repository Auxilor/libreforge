@file:Suppress("DEPRECATION")

package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.deprecationMessage
import com.willfp.libreforge.filters.impl.FilterAboveHealthPercent
import com.willfp.libreforge.filters.impl.FilterBlocks
import com.willfp.libreforge.filters.impl.FilterIsBoss
import com.willfp.libreforge.filters.impl.FilterDamageCause
import com.willfp.libreforge.filters.impl.FilterEntities
import com.willfp.libreforge.filters.impl.FilterFromSpawner
import com.willfp.libreforge.filters.impl.FilterFullyCharged
import com.willfp.libreforge.filters.impl.FilterFullyGrown
import com.willfp.libreforge.filters.impl.FilterIsBehindVictim
import com.willfp.libreforge.filters.impl.FilterIsNPC
import com.willfp.libreforge.filters.impl.FilterIsPassive
import com.willfp.libreforge.filters.impl.FilterItemDurabilityAbove
import com.willfp.libreforge.filters.impl.FilterItemDurabilityBelow
import com.willfp.libreforge.filters.impl.FilterItems
import com.willfp.libreforge.filters.impl.FilterOnMaxHealth
import com.willfp.libreforge.filters.impl.FilterOnlyBosses
import com.willfp.libreforge.filters.impl.FilterOnlyNonBosses
import com.willfp.libreforge.filters.impl.FilterPlayerName
import com.willfp.libreforge.filters.impl.FilterPlayerPlaced
import com.willfp.libreforge.filters.impl.FilterPotionEffect
import com.willfp.libreforge.filters.impl.FilterProjectiles
import com.willfp.libreforge.filters.impl.FilterText
import com.willfp.libreforge.filters.impl.FilterTextContains
import com.willfp.libreforge.filters.impl.FilterValueAbove
import com.willfp.libreforge.filters.impl.FilterValueBelow
import com.willfp.libreforge.filters.impl.FilterValueEquals
import com.willfp.libreforge.filters.impl.FilterVictimConditions
import com.willfp.libreforge.filters.impl.FilterVictimName

object Filters : Registry<Filter<*, *>>() {
    /**
     * Compile a [config] into a FilterList a given [context].
     */
    fun compile(config: Config, context: ViolationContext): FilterList {
        val blocks = mutableListOf<FilterBlock<*, *>>()

        for (key in config.getKeys(false)) {
            val filter = get(key) ?: get(key.removePrefix("not_")) ?: continue
            blocks += makeBlock(filter, config, context) ?: continue
        }

        return FilterList(blocks)
    }

    private fun <T, V> makeBlock(
        filter: Filter<T, V>,
        config: Config,
        context: ViolationContext
    ): FilterBlock<T, V>? {
        if (filter.deprecationMessage != null) {
            context.log(
                ConfigWarning(
                    filter.id,
                    "Filter ${filter.id} is deprecated: ${filter.deprecationMessage}. It will be removed in the future."
                )
            )
        }

        if (!filter.checkConfig(config, context)) {
            return null
        }

        val configKey = if (config.has("not_${filter.id}")) {
            "not_${filter.id}"
        } else {
            filter.id
        }

        val compileData = filter.makeCompileData(config, context, filter.getValue(config, null, configKey))
        return FilterBlock(filter, config, compileData)
    }

    init {
        register(FilterAboveHealthPercent)
        register(FilterBlocks)
        register(FilterDamageCause)
        register(FilterEntities)
        register(FilterFromSpawner)
        register(FilterFullyCharged)
        register(FilterFullyGrown)
        register(FilterIsBehindVictim)
        register(FilterIsNPC)
        register(FilterItems)
        register(FilterOnlyBosses)
        register(FilterOnlyNonBosses)
        register(FilterIsBoss)
        register(FilterOnMaxHealth)
        register(FilterPlayerName)
        register(FilterPlayerPlaced)
        register(FilterPotionEffect)
        register(FilterProjectiles)
        register(FilterText)
        register(FilterTextContains)
        register(FilterVictimConditions)
        register(FilterVictimName)
        register(FilterItemDurabilityAbove)
        register(FilterItemDurabilityBelow)
        register(FilterIsPassive)
        register(FilterValueAbove)
        register(FilterValueEquals)
        register(FilterValueBelow)
    }
}
