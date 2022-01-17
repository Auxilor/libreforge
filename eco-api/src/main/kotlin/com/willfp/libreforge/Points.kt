@file:JvmName("PointsUtils")

package com.willfp.libreforge

import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.events.PointsChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

private val keys = mutableMapOf<String, PersistentDataKey<Double>>()

private fun getKeyForType(type: String): PersistentDataKey<Double> {
    val existing = keys[type.lowercase()]
    return if (existing == null) {
        keys[type.lowercase()] = PersistentDataKey(
            LibReforgePlugin.instance.namespacedKeyFactory.create("points_${type.lowercase()}"),
            PersistentDataKeyType.DOUBLE,
            0.0
        )

        PlaceholderManager.registerPlaceholder(
            PlaceholderEntry(
                "points_${type.lowercase()}"
            ) { NumberUtils.format(it.getPoints(type)) }
        )

        getKeyForType(type)
    } else {
        existing
    }
}

fun Player.getPoints(type: String): Double {
    return PlayerProfile.load(this).read(getKeyForType(type))
}

fun Player.setPoints(type: String, points: Double) {
    val event = PointsChangeEvent(this, type, points)
    Bukkit.getPluginManager().callEvent(event)
    if (!(event.isCancelled)) {
        PlayerProfile.load(this).write(getKeyForType(type), event.amount)
    }
}

fun Player.givePoints(type: String, points: Double) {
    this.setPoints(type, this.getPoints(type) + points)
}
