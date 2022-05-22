package com.willfp.libreforge.effects.effects.particles

import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList

@Suppress("UNUSED")
object ParticleAnimations {
    private val BY_ID = HashBiMap.create<String, ParticleAnimation>()

    private val TRACE: ParticleAnimation = AnimationTrace
    private val GROUND_SPIRAL: ParticleAnimation = AnimationGroundSpiral
    private val HELIX: ParticleAnimation = AnimationHelix
    private val DOUBLE_HELIX: ParticleAnimation = AnimationDoubleHelix
    private val TWIRL: ParticleAnimation = AnimationTwirl
    private val CIRCLE: ParticleAnimation = AnimationCircle

    /**
     * Get ParticleAnimation matching id.
     *
     * @param id The id to query.
     * @return The matching ParticleAnimation, or null if not found.
     */
    fun getByID(id: String): ParticleAnimation? {
        return BY_ID[id]
    }

    /**
     * List of all registered ParticleAnimations.
     *
     * @return The conditions.
     */
    fun values(): List<ParticleAnimation> {
        return ImmutableList.copyOf(BY_ID.values)
    }

    /**
     * Add new ParticleAnimations.
     *
     * @param animation The ParticleAnimation to add.
     */
    fun addNewCondition(animation: ParticleAnimation) {
        BY_ID.remove(animation.id)
        BY_ID[animation.id] = animation
    }
}