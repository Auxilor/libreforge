@file:JvmName("PointsUtils")

package com.willfp.libreforge

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.math.MathContext
import com.willfp.eco.core.placeholder.PlayerDynamicPlaceholder
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.core.price.Prices
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.events.PointsChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import java.util.function.Function
import java.util.regex.Pattern

private val keys = mutableMapOf<String, PersistentDataKey<Double>>()

private fun getKeyForType(type: String): PersistentDataKey<Double> {
    val existing = keys[type.lowercase()]

    return if (existing == null) {
        val key = NamespacedKeyUtils.createEcoKey("points_${type.lowercase()}")

        keys[type.lowercase()] = PersistentDataKey(
            key,
            PersistentDataKeyType.DOUBLE,
            0.0
        )

        Prices.registerPriceFactory(PointPriceFactory(type.lowercase()))

        getKeyForType(type)
    } else {
        existing
    }
}

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
            return player.getPoints(type) >= getValue(player, multiplier)
        }

        override fun pay(player: Player, multiplier: Double) {
            player.takePoints(type, getValue(player, multiplier))
        }

        override fun giveTo(player: Player, multiplier: Double) {
            player.givePoints(type, getValue(player, multiplier))
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

fun initPointPlaceholders() {
    PlaceholderManager.registerPlaceholder(
        PlayerDynamicPlaceholder(
            LibReforgePlugin.instance,
            Pattern.compile("points_[a-zA-z_-]+")
        ) { args, player ->
            val type = args.removePrefix("points_")
            player.getPoints(type).toNiceString()
        }
    )
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
