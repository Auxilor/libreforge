package com.willfp.libreforge.effects

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.triggers.DispatchedTrigger

/**
 * A list of effect groups.
 */
class EffectList(
    effects: List<EffectBlock>
) : DelegatedList<EffectBlock>(effects.sortedBy {
    it.weight
}) {
    fun trigger(trigger: DispatchedTrigger) =
        this.forEach { it.trigger(trigger) }
}

/**
 * Create an empty [EffectList].
 */
fun emptyEffectList() = EffectList(emptyList())
