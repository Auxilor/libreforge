package com.willfp.libreforge.effects.effects.particles

import org.joml.Vector2f
import org.joml.Vector3f

fun Vector3f.copy() = Vector3f(
    this.x,
    this.y,
    this.z
)

fun DirectionVector.copy() = DirectionVector(
    this.x,
    this.y
)

class DirectionVector(
    yaw: Float,
    pitch: Float
) : Vector2f(
    yaw,
    pitch
) {
    val yaw = this.x
    val pitch = this.y
}

fun lerp(start: Double, end: Double, fraction: Double): Double =
    (start * (1 - fraction)) + (end * fraction)

fun Vector3f.rotate(yaw: Float, pitch: Float, roll: Float): Vector3f {
    return this.rotateY(yaw)
        .rotateX(pitch)
        .rotateZ(roll)
}
