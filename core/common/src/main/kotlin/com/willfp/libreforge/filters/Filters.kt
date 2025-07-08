@file:Suppress("DEPRECATION")

package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.deprecationMessage
import com.willfp.libreforge.filters.impl.FilterAboveHealthPercent
import com.willfp.libreforge.filters.impl.FilterAdvancements
import com.willfp.libreforge.filters.impl.FilterAltValueAbove
import com.willfp.libreforge.filters.impl.FilterAltValueBelow
import com.willfp.libreforge.filters.impl.FilterAltValueEquals
import com.willfp.libreforge.filters.impl.FilterBlocks
import com.willfp.libreforge.filters.impl.FilterDamageCause
import com.willfp.libreforge.filters.impl.FilterEnchant
import com.willfp.libreforge.filters.impl.FilterEntities
import com.willfp.libreforge.filters.impl.FilterFromSpawner
import com.willfp.libreforge.filters.impl.FilterFullyCharged
import com.willfp.libreforge.filters.impl.FilterFullyGrown
import com.willfp.libreforge.filters.impl.FilterHoneyLevelFull
import com.willfp.libreforge.filters.impl.FilterIsBehindVictim
import com.willfp.libreforge.filters.impl.FilterIsBoss
import com.willfp.libreforge.filters.impl.FilterIsExpressionTrue
import com.willfp.libreforge.filters.impl.FilterIsNPC
import com.willfp.libreforge.filters.impl.FilterIsPassive
import com.willfp.libreforge.filters.impl.FilterItemDurabilityAbove
import com.willfp.libreforge.filters.impl.FilterItemDurabilityAbovePercent
import com.willfp.libreforge.filters.impl.FilterItemDurabilityBelow
import com.willfp.libreforge.filters.impl.FilterItemDurabilityBelowPercent
import com.willfp.libreforge.filters.impl.FilterItems
import com.willfp.libreforge.filters.impl.FilterOnMaxHealth
import com.willfp.libreforge.filters.impl.FilterOnlyBosses
import com.willfp.libreforge.filters.impl.FilterOnlyNonBosses
import com.willfp.libreforge.filters.impl.FilterPlayerName
import com.willfp.libreforge.filters.impl.FilterPlayerPlaced
import com.willfp.libreforge.filters.impl.FilterPotionEffect
import com.willfp.libreforge.filters.impl.FilterProjectiles
import com.willfp.libreforge.filters.impl.FilterSheepColor
import com.willfp.libreforge.filters.impl.FilterSpawnerEntity
import com.willfp.libreforge.filters.impl.FilterSwept
import com.willfp.libreforge.filters.impl.FilterText
import com.willfp.libreforge.filters.impl.FilterTextContains
import com.willfp.libreforge.filters.impl.FilterThisItem
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
            if (key.startsWith("not_")) {
                val filter = get(key.removePrefix("not_")) ?: continue
                blocks += makeBlock(filter, config, true, context) ?: continue
            } else {
                val filter = get(key) ?: continue
                blocks += makeBlock(filter, config, false, context) ?: continue
            }
        }

        return FilterList(blocks)
    }

    private fun <T, V> makeBlock(
        filter: Filter<T, V>,
        config: Config,
        inverted: Boolean,
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

        val configKey = if (inverted) {
            "not_${filter.id}"
        } else {
            filter.id
        }

        val compileData = filter.makeCompileData(config, context, filter.getValue(config, null, configKey))
        return FilterBlock(filter, config, compileData, inverted)
    }

    init {
        register(FilterAboveHealthPercent)
        register(FilterAdvancements)
        register(FilterAltValueAbove)
        register(FilterAltValueBelow)
        register(FilterAltValueEquals)
        register(FilterBlocks)
        register(FilterDamageCause)
        register(FilterEnchant)
        register(FilterEntities)
        register(FilterFromSpawner)
        register(FilterFullyCharged)
        register(FilterFullyGrown)
        register(FilterHoneyLevelFull)
        register(FilterIsBehindVictim)
        register(FilterIsBoss)
        register(FilterIsExpressionTrue)
        register(FilterIsNPC)
        register(FilterIsPassive)
        register(FilterItemDurabilityAbove)
        register(FilterItemDurabilityAbovePercent)
        register(FilterItemDurabilityBelow)
        register(FilterItemDurabilityBelowPercent)
        register(FilterItems)
        register(FilterOnlyBosses)
        register(FilterOnlyNonBosses)
        register(FilterOnMaxHealth)
        register(FilterPlayerName)
        register(FilterPlayerPlaced)
        register(FilterPotionEffect)
        register(FilterProjectiles)
        register(FilterSheepColor)
        register(FilterSpawnerEntity)
        register(FilterSwept)
        register(FilterText)
        register(FilterTextContains)
        register(FilterThisItem)
        register(FilterValueAbove)
        register(FilterValueBelow)
        register(FilterValueEquals)
        register(FilterVictimConditions)
        register(FilterVictimName)
    }
}
