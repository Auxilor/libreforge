package com.willfp.libreforge

import org.bukkit.Location
import org.bukkit.block.Block
import kotlin.math.roundToInt

inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String): T? {
    return try {
        enumValueOf<T>(name)
    } catch (e: IllegalArgumentException) {
        null
    }
}

val Any.deprecationMessage: String?
    get() {
        val annotation = this::class.java.getAnnotation(Deprecated::class.java)
        return annotation?.message
    }

fun Location.getNearbyBlocks(
    x: Double,
    y: Double,
    z: Double
): Collection<Block> {
    val blocks = mutableListOf<Block>()

    val xRadius = (x / 2).roundToInt()
    val yRadius = (y / 2).roundToInt()
    val zRadius = (z / 2).roundToInt()

    for (xPos in -xRadius..xRadius) {
        for (yPos in -yRadius..yRadius) {
            for (zPos in -zRadius..zRadius) {
                blocks.add(this.clone().add(xPos.toDouble(), yPos.toDouble(), zPos.toDouble()).block)
            }
        }
    }

    return blocks
}

fun Location.getNearbyBlocksInSphere(
    radius: Double
): Collection<Block> = getNearbyBlocks(radius, radius, radius)
    .filter { it.location.distanceSquared(this) <= radius * radius }
