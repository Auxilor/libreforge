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
