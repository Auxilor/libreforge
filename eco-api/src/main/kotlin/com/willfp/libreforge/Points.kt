@file:JvmName("PointsUtils")

package com.willfp.libreforge

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.events.PointsChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

private val keys = mutableMapOf<String, PersistentDataKey<Double>>()

private val knownPointsKey = PersistentDataKey(
    LibReforgePlugin.instance.namespacedKeyFactory.create("known_points"),
    PersistentDataKeyType.STRING,
    ""
)

private fun getKeyForType(type: String): PersistentDataKey<Double> {
    val existing = keys[type.lowercase()]

    return if (existing == null) {
        val key = if (type.startsWith("g_")) {
            NamespacedKeyUtils.createEcoKey("points_${type.lowercase()}")
        } else {
            LibReforgePlugin.instance.namespacedKeyFactory.create("points_${type.lowercase()}")
        }

        keys[type.lowercase()] = PersistentDataKey(
            key,
            PersistentDataKeyType.DOUBLE,
            0.0
        )

        PlaceholderManager.registerPlaceholder(
            PlaceholderEntry(
                LibReforgePlugin.instance,
                "points_${type.lowercase()}"
            ) { NumberUtils.format(it.getPoints(type)) }
        )

        val knownPoints = Bukkit.getServer().profile.read(knownPointsKey).split(";").toMutableSet()
        knownPoints.add(type)
        Bukkit.getServer().profile.write(knownPointsKey, knownPoints.joinToString(";"))

        getKeyForType(type)
    } else {
        existing
    }
}

fun initPointPlaceholders() {
    Bukkit.getServer().profile.read(knownPointsKey).split(";").forEach { getKeyForType(it) }
}

fun Player.getPoints(type: String): Double {
    return this.profile.read(getKeyForType(type))
}

fun Player.setPoints(type: String, points: Double) {
    val event = PointsChangeEvent(this, type, points)
    Bukkit.getPluginManager().callEvent(event)
    if (!(event.isCancelled)) {
        this.profile.write(getKeyForType(type), event.amount)
    }
}

fun Player.givePoints(type: String, points: Double) {
    this.setPoints(type, this.getPoints(type) + points)
}
