package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.placeholder.PlayerDynamicPlaceholder
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.toNiceString
import org.bukkit.entity.Player
import java.util.regex.Pattern

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

fun pointsPlaceholder(
    plugin: EcoPlugin
): PlayerDynamicPlaceholder {
    return PlayerDynamicPlaceholder(
        plugin,
        Pattern.compile("points_[a-zA-z_-]+")
    ) { args, player ->
        val type = args.removePrefix("points_")
        player.points[type].toNiceString()
    }
}

val Player.points: PointsMap
    get() = PointsMap(this)

fun String.toFriendlyPointName() =
    plugin.configYml.getFormattedString("point-names.$this")
