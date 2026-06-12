package com.willfp.libreforge.effects.impl

/**
 * Schema for a single firework effect subsection (see [EffectFirework] and [EffectShootFirework]).
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 * Non-null properties are required keys; nullable properties are optional keys.
 * A List<String> property with a listOf(...) default is a fixed set of choices (dropdown);
 * a List<String> property without a default is a free list of user entries.
 *
 * @property type The explosion shape.
 * @property colors The hex colors of the explosion.
 * @property fadeColors The hex colors the explosion fades to.
 * @property trail Whether the firework leaves a trail.
 * @property flicker Whether the explosion flickers.
 */
data class FireworkEffectSpec(
    val type: List<String> = listOf("ball", "ball_large", "star", "burst", "creeper"),
    val colors: List<String>?,
    val fadeColors: List<String>?,
    val trail: Boolean?,
    val flicker: Boolean?,
)
