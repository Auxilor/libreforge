package com.willfp.libreforge

import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.normalize
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun Float3.toLocation(world: World) =
    Location(world, x.toDouble(), y.toDouble(), z.toDouble())

fun Location.toFloat3() =
    Float3(x.toFloat(), y.toFloat(), z.toFloat())

fun Vector.toFloat3() =
    Float3(x.toFloat(), y.toFloat(), z.toFloat())

fun Float3.toVector() =
    Vector(x.toDouble(), y.toDouble(), z.toDouble())

val Float3.xz: Float2
    get() = Float2(x, z)

operator fun Float3.plusAssign(other: Float3) {
    this.x += other.x
    this.y += other.y
    this.z += other.z
}

operator fun Float2.plusAssign(other: Float2) {
    this.x += other.x
    this.y += other.y
}

operator fun Float3.minusAssign(other: Float3) {
    this.x -= other.x
    this.y -= other.y
    this.z -= other.z
}

operator fun Float2.minusAssign(other: Float2) {
    this.x -= other.x
    this.y -= other.y
}

fun Float3.normalize() = normalize(this)

fun Float2.normalize() = normalize(this)

fun Float2.angle(other: Float2): Float {
    val dot = this.dot(other)
    val det = this.det(other)
    return Math.toDegrees(atan2(det.toDouble(), dot.toDouble())).toFloat()
}

fun Float2.dot(other: Float2): Float {
    return x * other.x + y * other.y
}

fun Float2.det(other: Float2): Float {
    return x * other.y - y * other.x
}

fun Float3.rotate(yaw: Float, pitch: Float, roll: Float): Float3 {
    val (cosYaw, sinYaw) = yaw.toDouble().let { cos(it) to sin(it) }
    val (cosPitch, sinPitch) = pitch.toDouble().let { cos(it) to sin(it) }
    val (cosRoll, sinRoll) = roll.toDouble().let { cos(it) to sin(it) }

    // Rotate around Y-axis (yaw)
    val x1 = x * cosYaw.toFloat() + z * sinYaw.toFloat()
    val z1 = (-x * sinYaw + z * cosYaw).toFloat()

    // Rotate around X-axis (pitch)
    val y2 = (y * cosPitch - z1 * sinPitch).toFloat()
    val z2 = (y * sinPitch + z1 * cosPitch).toFloat()

    // Rotate around Z-axis (roll)
    val x3 = (x1 * cosRoll - y2 * sinRoll).toFloat()
    val y3 = (x1 * sinRoll + y2 * cosRoll).toFloat()

    return Float3(x3, y3, z2)
}


fun Float3.distance(other: Float3) =
    sqrt((x - other.x).pow(2) + (y - other.y).pow(2) + (z - other.z).pow(2))

fun Float2.distance(other: Float2) =
    sqrt((x - other.x).pow(2) + (y - other.y).pow(2))

fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

fun lerp(a: Double, b: Double, t: Double) = a + (b - a) * t
