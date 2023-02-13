package com.willfp.libreforge

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.entity.Player

class PointsMap(
    private val player: Player
) {
    operator fun get(key: String): Double {
        val dataKey = PersistentDataKey(
            NamespacedKeyUtils.createEcoKey("points_${key.lowercase()}"),
            PersistentDataKeyType.DOUBLE,
            0.0
        )

        return player.profile.read(dataKey)
    }

    operator fun set(key: String, value: Double) {
        val dataKey = PersistentDataKey(
            NamespacedKeyUtils.createEcoKey("points_${key.lowercase()}"),
            PersistentDataKeyType.DOUBLE,
            0.0
        )

        player.profile.write(dataKey, value)
    }
}

val Player.points: PointsMap
    get() = PointsMap(this)

fun String.toFriendlyPointName() =
    LibreforgeConfig.getFormattedString("point-names.$this")
