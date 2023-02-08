package com.willfp.libreforge

import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.normalize
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import kotlin.math.atan2

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
