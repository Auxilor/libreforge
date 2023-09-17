package com.willfp.libreforge.effects

import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList

/**
 * EffectContext exists in order to abstract out some compile logic between
 * element compilation and rich chain compilation.
 */
data class EffectContext internal constructor(
    val arguments: EffectArgumentList,
    val conditions: ConditionList,
    val mutators: MutatorList,
    val filters: FilterList
)
