package com.willfp.libreforge.effects.effects.particles

import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos

object AnimationSlice : ParticleAnimation(
    "slice"
) {
    override val particleAmount = 1

    override fun getParticleLocation(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f
    ): Vector3f {
        val yaw = Math.toRadians(playerDirection.yaw.toDouble()).toFloat()
        val pitch = Math.toRadians(playerDirection.pitch.toDouble()).toFloat()

        val mat = Matrix4f()
            .translate(0f, 1.6f, 0f)
            .rotate(yaw, 0f, -1f, 0f)
            .rotate(pitch, 1f, 0f, 0f)
            .rotate(PI.toFloat() * 0.25f, 0f, 0f, 1f)

        val tickRadians = Math.toRadians(tick.toDouble())
        val dx = cos(tickRadians) * 1.5
        val dz = cos(tickRadians) * 1.5

        val offset = mat.transformPosition(
            Vector3f(
                dx.toFloat(),
                0f,
                dz.toFloat()
            )
        )

        return playerLocation.add(offset)
    }

    override fun shouldStopTicking(
        tick: Int,
        playerLocation: Vector3f,
        playerDirection: DirectionVector,
        location: Vector3f,
        lastLocation: Vector3f
    ): Boolean {
        return tick > 210
    }
}
