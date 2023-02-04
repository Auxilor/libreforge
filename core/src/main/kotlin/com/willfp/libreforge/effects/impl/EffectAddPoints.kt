package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.DefaultHashMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.points
import org.bukkit.entity.Player
import java.util.UUID

object EffectAddPoints : Effect<NoCompileData>("add_points") {
    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("amount", "You must specify the amount of points!")
    }

    private val tracker = DefaultHashMap<UUID, MutableMap<UUID, AddedPoint>>(mutableMapOf())

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, compileData: NoCompileData) {
        val added = tracker[player.uniqueId]
        val point = config.getString("type")
        val amount = config.getDoubleFromExpression("amount", player)

        added[identifiers.uuid] = AddedPoint(
            point,
            amount
        )

        player.points[point] += amount

        tracker[player.uniqueId] = added
    }

    override fun onDisable(player: Player, identifiers: Identifiers) {
        val added = tracker[player.uniqueId]
        val addedPoint = added[identifiers.uuid] ?: return
        added -= identifiers.uuid

        player.points[addedPoint.point] -= addedPoint.amount

        tracker[player.uniqueId] = added
    }

    private data class AddedPoint(
        val point: String,
        val amount: Double
    )
}
