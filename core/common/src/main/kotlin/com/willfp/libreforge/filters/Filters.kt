@file:Suppress("DEPRECATION")

package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.deprecationMessage
import com.willfp.libreforge.filters.impl.*

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
        register(FilterAdvancements)
        register(FilterAboveHealthPercent)
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
        register(FilterOnMaxHealth)
        register(FilterOnlyBosses)
        register(FilterOnlyNonBosses)
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
        register(FilterTamedEntity)
        register(FilterIsTamedEntityOwner)
    }
}
