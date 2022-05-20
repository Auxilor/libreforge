package com.willfp.libreforge.effects.effects.particles

import org.joml.Vector3f
import java.util.*

abstract class ParticleAnimation(
    val id: String
) {
    init {
        register()
    }

    private fun register() {
        ParticleAnimations.addNewCondition(this)
    }

    open val useEyeLocation: Boolean = false

    abstract val particleAmount: Int

    abstract fun getParticleLocations(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f
    ): Iterable<Vector3f>

    abstract fun shouldStopTicking(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f,
        lastLocation: Vector3f
    ): Boolean

    override fun equals(other: Any?): Boolean {
        if (other !is ParticleAnimation) {
            return false
        }
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}
