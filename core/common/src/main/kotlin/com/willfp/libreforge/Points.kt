package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
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
        private val multipliers = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build<UUID, Double>()

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
            return multipliers.getIfPresent(player.uniqueId) ?: 1.0
        }

        override fun setMultiplier(player: Player, multiplier: Double) {
            multipliers.put(player.uniqueId, multiplier)
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
    companion object {
        private val keyCache = ConcurrentHashMap<String, PersistentDataKey<Double>>()

        private fun getKey(type: String): PersistentDataKey<Double> {
            return keyCache.computeIfAbsent(type) {
                PersistentDataKey(
                    NamespacedKeyUtils.createEcoKey("points_${it.lowercase()}"),
                    PersistentDataKeyType.DOUBLE,
                    0.0
                )
            }
        }
    }

    private fun initializeIfNeeded(type: String) {
        if (type in initializedPoints) {
            return
        }

        initializedPoints += type
        Prices.registerPriceFactory(PointPriceFactory(type))
    }

    operator fun get(key: String): Double {
        initializeIfNeeded(key)

        return profile.read(getKey(key))
    }

    fun add(key: String, amount: Double) {
        set(key, get(key) + amount)
    }

    operator fun set(key: String, value: Double) {
        initializeIfNeeded(key)

        profile.write(getKey(key), value)
    }

    override fun toString(): String {
        return initializedPoints.joinToString(", ") {
            "$it -> ${get(it)}"
        }
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
