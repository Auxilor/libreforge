@file:JvmName("PointsUtils")

package com.willfp.libreforge

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.events.EffectActivateEvent
import com.willfp.libreforge.events.PointsChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

private val keys = mutableMapOf<String, PersistentDataKey<Double>>()

private val knownPointsKey = PersistentDataKey(
    LibReforgePlugin.instance.namespacedKeyFactory.create("known_points"),
    PersistentDataKeyType.STRING,
    ""
).server()

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
        ).player()

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

private fun String.toFriendlyPointName(): String {
    val config = LibReforgePlugin.instance.configYml.getSubsectionOrNull("point-names") ?: return this
    return config.getStringOrNull(this) ?: return this
}

class PointCostHandler : Listener {
    @EventHandler
    fun onTrigger(event: EffectActivateEvent) {
        val player = event.player
        val config = event.config
        val effect = event.effect

        if (config.has("point_cost")) {
            val cost = config.getDoubleFromExpression("point_cost.cost", player)
            val type = config.getString("point_cost.type")

            if (player.getPoints(type) < cost) {
                effect.sendCannotAffordTypeMessage(player, cost, type.toFriendlyPointName())
                event.isCancelled = true
                return
            }

            player.setPoints(type, player.getPoints(type) - cost)
        }
    }
}
