package com.willfp.libreforge.effects.impl

/**
 * Schema for a single weighted item group subsection (see [EffectDropWeightedRandomItem]).
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 * Non-null properties are required keys; nullable properties are optional keys.
 *
 * @property items The items in this group, in eco item lookup format.
 * @property weight The relative weight of this group when choosing what to drop.
 */
data class WeightedItemSpec(
    val items: List<String>,
    val weight: Double,
)
