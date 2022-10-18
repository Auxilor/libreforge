package com.willfp.libreforge.effects.effects.particles

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import org.joml.Vector2d
import org.joml.Vector2f
import org.joml.Vector3f

fun Vector3f.copy() = Vector3f(
    this.x,
    this.y,
    this.z
)

fun Vector2f.copy() = Vector2f(
    this.x,
    this.y
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
    val yaw: Float
        get() = this.x
    val pitch: Float
        get() = this.y
}

fun lerp(start: Double, end: Double, fraction: Double): Double =
    (start * (1 - fraction)) + (end * fraction)

fun Vector3f.rotate(yaw: Float, pitch: Float, roll: Float): Vector3f {
    return this.rotateY(yaw)
        .rotateX(pitch)
        .rotateZ(roll)
}

fun Vector3f.toLocation(world: World) = Location(
    world,
    this.x.toDouble(),
    this.y.toDouble(),
    this.z.toDouble()
)

fun Location.toVector3f() = Vector3f(
    this.x.toFloat(),
    this.y.toFloat(),
    this.z.toFloat()
)

fun Vector.toVector3f() = Vector3f(
    this.x.toFloat(),
    this.y.toFloat(),
    this.z.toFloat()
)

fun Location.toDirectionVector() = DirectionVector(
    this.yaw,
    this.pitch
)
