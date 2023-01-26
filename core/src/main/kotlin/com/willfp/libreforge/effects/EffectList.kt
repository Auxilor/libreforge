package com.willfp.libreforge.effects

import com.willfp.libreforge.DelegatedList

/**
 * A list of effect groups.
 */
class EffectList(
    effects: List<EffectGroup>
) : DelegatedList<EffectGroup>() {
    init {
        this.list += effects.sortedBy {
            val total = it.effects.sumOf { block -> block.effect.runOrder.weight }
            total / it.effects.size
        }
    }
}
