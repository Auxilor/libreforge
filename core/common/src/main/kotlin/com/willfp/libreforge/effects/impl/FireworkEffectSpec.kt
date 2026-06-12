package com.willfp.libreforge.effects.impl

/**
 * Schema for a single firework effect subsection (see [EffectFirework] and [EffectShootFirework]).
 *
 * Documentation-only: parsed from source by the wiki scanner, never instantiated at runtime.
 * Non-null properties are required keys; nullable properties are optional keys.
 *
 * @property type The explosion shape: ball, ball_large, star, burst, creeper.
 * @property colors The hex colors of the explosion.
 * @property fadeColors The hex colors the explosion fades to.
 * @property trail Whether the firework leaves a trail.
 * @property flicker Whether the explosion flickers.
 */
data class FireworkEffectSpec(
    val type: String,
    val colors: List<String>?,
    val fadeColors: List<String>?,
    val trail: Boolean?,
    val flicker: Boolean?,
)
