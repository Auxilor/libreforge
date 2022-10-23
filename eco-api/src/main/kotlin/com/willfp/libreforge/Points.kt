@file:JvmName("PointsUtils")

package com.willfp.libreforge

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.events.PointsChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

private val keys = mutableMapOf<String, PersistentDataKey<Double>>()

private val registeredPointsKey = PersistentDataKey(
    LibReforgePlugin.instance.namespacedKeyFactory.create("registered_points"),
    PersistentDataKeyType.STRING_LIST,
    emptyList()
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
            PlayerPlaceholder(
                LibReforgePlugin.instance,
                "points_${type.lowercase()}"
            ) { NumberUtils.format(it.getPoints(type)) }
        )

        val knownPoints = Bukkit.getServer().profile.read(registeredPointsKey).toMutableSet()
        knownPoints.add(type)
        Bukkit.getServer().profile.write(registeredPointsKey, knownPoints.toList())

        getKeyForType(type)
    } else {
        existing
    }
}

fun initPointPlaceholders() {
    Bukkit.getServer().profile.read(registeredPointsKey).forEach { getKeyForType(it) }
}

fun Player.getPoints(type: String): Double {
    return this.profile.read(getKeyForType(type))
}

fun Player.setPoints(type: String, points: Double) {
    val event = PointsChangeEvent(this, type, points, this.getPoints(type))
    Bukkit.getPluginManager().callEvent(event)
    if (!(event.isCancelled)) {
        this.profile.write(getKeyForType(type), event.amount)
    }
}

fun Player.givePoints(type: String, points: Double) {
    this.setPoints(type, this.getPoints(type) + points)
}

fun Player.takePoints(type: String, points: Double) {
    this.setPoints(type, this.getPoints(type) - points)
}

fun String.toFriendlyPointName(): String {
    val config = LibReforgePlugin.instance.configYml.getSubsectionOrNull("point-names") ?: return this
    return config.getStringOrNull(this) ?: return this
}
