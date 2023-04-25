package com.willfp.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.math.MathContext
import com.willfp.eco.core.placeholder.PlayerDynamicPlaceholder
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.core.price.Prices
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.toNiceString
import org.bukkit.entity.Player
import java.util.UUID
import java.util.function.Function
import java.util.regex.Pattern

class PointPriceFactory(private val type: String) : PriceFactory {
    override fun getNames() = listOf(type)

    override fun create(baseContext: MathContext, function: Function<MathContext, Double>): Price {
        return PricePoint(type, baseContext) { function.apply(it) }
    }

    private class PricePoint(
        private val type: String,
        private val baseContext: MathContext,
        private val function: (MathContext) -> Double
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
            return function(MathContext.copyWithPlayer(baseContext, player)) * getMultiplier(player) * multiplier
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
    private val player: Player
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

        return player.profile.read(dataKey)
    }

    operator fun set(key: String, value: Double) {
        initializeIfNeeded(key)

        if (value < 0) {
            throw IllegalArgumentException("Points cannot be negative! (Tried to set $key to $value)")
        }

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
