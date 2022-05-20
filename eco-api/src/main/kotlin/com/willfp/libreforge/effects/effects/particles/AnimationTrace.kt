package com.willfp.libreforge.effects.effects.particles

import org.joml.Vector3f

object AnimationTrace : ParticleAnimation(
    "trace"
) {
    override val particleAmount = 1
    override val useEyeLocation = true

    override fun getParticleLocations(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f
    ): Iterable<Vector3f> {
        val perTick = 8

        val t = tick * perTick
        return (t until t + perTick).map {
            location.copy().add(
                playerLocation.copy().sub(location).normalize().mul(0.2f * t)
            )
        }
    }

    override fun shouldStopTicking(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f,
        lastLocation: Vector3f
    ): Boolean {
        return playerLocation.distance(lastLocation) < 1 || tick > 100
    }
}
