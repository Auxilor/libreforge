package com.willfp.libreforge.effects.impl.particles

/*

Schemas for the particle_args subsection of EffectParticleAnimation.

The keys of particle_args depend on the animation chosen, so there is one schema per
ParticleAnimation rather than a single schema on the effect. Each animation points at its
own schema through ParticleAnimation.schema, and the effect reaches them through
inherit("particle_args").

All of these are documentation-only: parsed from source by the wiki scanner, never
instantiated at runtime. Non-null properties are required keys; nullable properties are
optional keys. Every value is read as an expression, so placeholders and maths are
supported throughout.

Hyphenated config keys are written with backticks so the property name is the config key
verbatim.

 */

/**
 * Schema for the particle_args of the circle animation.
 *
 * @property radius The radius of the circle in blocks.
 * @property duration How long in ticks the animation runs for, and the period of one full circle.
 * @property height The height above the anchor location to draw the circle at.
 * @property pitch The pitch to rotate the circle by, in degrees. 0 draws a flat, horizontal circle.
 * @property roll The roll to rotate the circle by, in degrees. 0 draws a flat, horizontal circle.
 * @property `tick-multiplier` How many animation steps to advance per tick. Raise it to draw the
 *           shape faster or more densely; omit it to advance one step per tick.
 */
data class ParticleAnimationCircleSpec(
    val radius: String,
    val duration: String,
    val height: String,
    val pitch: String,
    val roll: String,
    val `tick-multiplier`: String?,
)

/**
 * Schema for the particle_args of the double_helix animation.
 *
 * @property height The total height the helix climbs over one duration.
 * @property duration How long in ticks the animation runs for.
 * @property speed How quickly the helix rotates as it climbs.
 * @property radius The radius of each strand of the helix in blocks.
 * @property `tick-multiplier` How many animation steps to advance per tick. Raise it to draw the
 *           shape faster or more densely; omit it to advance one step per tick.
 */
data class ParticleAnimationDoubleHelixSpec(
    val height: String,
    val duration: String,
    val speed: String,
    val radius: String,
    val `tick-multiplier`: String?,
)

/**
 * Schema for the particle_args of the ground_spiral animation.
 *
 * @property scalar How quickly the spiral rotates around the entity.
 * @property `distance-scalar` How quickly the spiral expands outwards from the entity.
 * @property duration How long in ticks the animation runs for.
 * @property `tick-multiplier` How many animation steps to advance per tick. Raise it to draw the
 *           shape faster or more densely; omit it to advance one step per tick.
 */
data class ParticleAnimationGroundSpiralSpec(
    val scalar: String,
    val `distance-scalar`: String,
    val duration: String,
    val `tick-multiplier`: String?,
)

/**
 * Schema for the particle_args of the helix animation.
 *
 * @property height The total height the helix climbs over one duration.
 * @property duration How long in ticks the animation runs for.
 * @property speed How quickly the helix rotates as it climbs.
 * @property radius The radius of the helix in blocks.
 * @property `tick-multiplier` How many animation steps to advance per tick. Raise it to draw the
 *           shape faster or more densely; omit it to advance one step per tick.
 */
data class ParticleAnimationHelixSpec(
    val height: String,
    val duration: String,
    val speed: String,
    val radius: String,
    val `tick-multiplier`: String?,
)

/**
 * Schema for the particle_args of the projectile_trail animation.
 *
 * @property gap How many ticks to wait between each particle of the trail.
 * @property `tick-multiplier` How many animation steps to advance per tick. Raise it to draw the
 *           shape faster or more densely; omit it to advance one step per tick.
 */
data class ParticleAnimationProjectileTrailSpec(
    val gap: String,
    val `tick-multiplier`: String?,
)

/**
 * Schema for the particle_args of the trace animation.
 *
 * @property spacing The distance between each particle along the traced line.
 * @property `tick-multiplier` How many animation steps to advance per tick. Raise it to draw the
 *           shape faster or more densely; omit it to advance one step per tick.
 */
data class ParticleAnimationTraceSpec(
    val spacing: String,
    val `tick-multiplier`: String?,
)

/**
 * Schema for the particle_args of the twirl animation.
 *
 * @property `small-radius` The radius the twirl starts at, in blocks.
 * @property `large-radius` The radius the twirl ends at, in blocks.
 * @property duration How long in ticks the animation runs for.
 * @property `start-height` The height above the anchor location the twirl starts at.
 * @property `end-height` The height above the anchor location the twirl ends at.
 * @property speed How quickly the twirl rotates as it grows.
 * @property `tick-multiplier` How many animation steps to advance per tick. Raise it to draw the
 *           shape faster or more densely; omit it to advance one step per tick.
 */
data class ParticleAnimationTwirlSpec(
    val `small-radius`: String,
    val `large-radius`: String,
    val duration: String,
    val `start-height`: String,
    val `end-height`: String,
    val speed: String,
    val `tick-multiplier`: String?,
)

/**
 * Schema for the particle_args of the wing_tips animation.
 *
 * The wing tips are placed relative to the entity's facing direction, so the animation takes
 * no shape arguments of its own.
 *
 * @property `tick-multiplier` How many animation steps to advance per tick. Raise it to draw the
 *           shape faster or more densely; omit it to advance one step per tick.
 */
data class ParticleAnimationWingTipsSpec(
    val `tick-multiplier`: String?,
)
