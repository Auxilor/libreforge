package com.willfp.libreforge.effects.effects.particles

import org.joml.Matrix3f
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

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
    val yawMatrix = Matrix3f(
        cos(yaw), -sin(yaw), 0f,
        sin(yaw), cos(yaw), 0f,
        0f, 0f, 1f
    )

    val pitchMatrix = Matrix3f(
        cos(pitch), 0f, sin(pitch),
        0f, 1f, 0f,
        -sin(pitch), 0f, cos(pitch)
    )

    val rollMatrix = Matrix3f(
        1f, 0f, 0f,
        0f, cos(roll), -sin(roll),
        0f, sin(roll), cos(roll)
    )

    val rotationMatrix = Matrix3f()
    yawMatrix.mul(pitchMatrix, rotationMatrix)
    rotationMatrix.mul(rotationMatrix, rollMatrix)

    return this.mul(rotationMatrix)
}
