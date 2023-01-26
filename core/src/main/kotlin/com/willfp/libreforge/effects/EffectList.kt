package com.willfp.libreforge.effects

import com.willfp.libreforge.DelegatedList

/**
 * A list of effect groups.
 */
class EffectList(
    effects: List<EffectBlock>
) : DelegatedList<EffectBlock>() {
    init {
        this.list += effects.sortedBy {
            val total = it.effects.sumOf { element -> element.effect.runOrder.weight }
            total / it.effects.size
        }
    }
}
