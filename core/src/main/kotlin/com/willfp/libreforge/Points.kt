package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.Profile
import com.willfp.eco.core.data.ServerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.placeholder.DynamicPlaceholder
import com.willfp.eco.core.placeholder.PlayerDynamicPlaceholder
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.core.price.Prices
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.toNiceString
import org.bukkit.entity.Player
import java.util.UUID
import java.util.regex.Pattern

class PointPriceFactory(private val type: String) : PriceFactory {
    override fun getNames() = listOf(type)

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double>): Price {
        return PricePoint(type, baseContext) { function.get(it) }
    }

    private class PricePoint(
        private val type: String,
        private val baseContext: PlaceholderContext,
        private val function: (PlaceholderContext) -> Double
    ) : Price {
        private val multipliers = mutableMapOf<UUID, Double>()

        override fun canAfford(player: Player, multiplier: Double): Boolean {
            return player.points[type] >= getValue(player, multiplier)
        }

        override fun pay(player: Player, multiplier: Double) {
            player.points[type] -= getValue(player, multiplier)
        }

        override fun giveTo(player: Player, multiplier: Double) {
            player.points[type] += getValue(player, multiplier)
        }

        override fun getValue(player: Player, multiplier: Double): Double {
            return function(baseContext.copyWithPlayer(player)) * getMultiplier(player) * multiplier
        }

        override fun getMultiplier(player: Player): Double {
            return multipliers[player.uniqueId] ?: 1.0
        }

        override fun setMultiplier(player: Player, multiplier: Double) {
            multipliers[player.uniqueId] = multiplier
        }

        override fun getIdentifier(): String {
            return "libreforge:point_$type"
        }
    }
}

private val initializedPoints = mutableSetOf<String>()

class PointsMap(
    private val profile: Profile
) {
    private fun initializeIfNeeded(type: String) {
        if (type in initializedPoints) {
            return
        }

        initializedPoints += type
        Prices.registerPriceFactory(PointPriceFactory(type))
    }

    operator fun get(key: String): Double {
        initializeIfNeeded(key)

        val dataKey = PersistentDataKey(
            NamespacedKeyUtils.createEcoKey("points_${key.lowercase()}"),
            PersistentDataKeyType.DOUBLE,
            0.0
        )

        return profile.read(dataKey)
    }

    operator fun set(key: String, value: Double) {
        initializeIfNeeded(key)

        val dataKey = PersistentDataKey(
            NamespacedKeyUtils.createEcoKey("points_${key.lowercase()}"),
            PersistentDataKeyType.DOUBLE,
            0.0
        )

        profile.write(dataKey, value)
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

fun globalPointsPlaceholder(
    plugin: EcoPlugin
): DynamicPlaceholder {
    return DynamicPlaceholder(
        plugin,
        Pattern.compile("global_points_[a-zA-z_-]+")
    ) { args ->
        val type = args.removePrefix("global_points_")
        globalPoints[type].toNiceString()
    }
}

val globalPoints: PointsMap
    get() = PointsMap(ServerProfile.load())

val Player.points: PointsMap
    get() = PointsMap(this.profile)

fun String.toFriendlyPointName() =
    plugin.configYml.getFormattedStringOrNull("point-names.$this") ?: this
