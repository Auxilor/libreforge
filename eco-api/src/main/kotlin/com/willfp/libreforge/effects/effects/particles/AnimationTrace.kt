package com.willfp.libreforge.effects.effects.particles

import org.joml.Vector3f

object AnimationTrace : ParticleAnimation(
    "trace"
) {
    override val particleAmount = 1

    override fun getParticleLocation(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f
    ): Vector3f {
        return location.add(
            location.sub(playerLocation).normalize().mul(0.1f * tick)
        )
    }

    override fun shouldStopTicking(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f,
        lastLocation: Vector3f
    ): Boolean {
        return playerLocation.distance(location) < 0.1 || tick > 100
    }
}
