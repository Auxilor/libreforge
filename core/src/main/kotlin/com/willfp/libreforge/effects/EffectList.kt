package com.willfp.libreforge.effects

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.triggers.DispatchedTrigger

/**
 * A list of effect groups.
 */
class EffectList(
    effects: List<EffectBlock>
) : DelegatedList<EffectBlock>(effects.sortedBy {
    val total = it.effects.sumOf { element -> element.effect.runOrder.weight }
    total / it.effects.size.coerceAtLeast(1)
}) {
    fun trigger(trigger: DispatchedTrigger) =
        this.forEach { it.trigger(trigger) }
}

/**
 * Create an empty [EffectList].
 */
fun emptyEffectList() = EffectList(emptyList())
