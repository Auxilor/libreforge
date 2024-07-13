package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.nestedMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.points
import org.bukkit.entity.Player
import java.util.UUID

object EffectAddPoints : Effect<NoCompileData>("add_points") {
    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("amount", "You must specify the amount of points!")
    }

    private val tracker = nestedMap<UUID, UUID, AddedPoint>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return

        val point = config.getString("type")
        val amount = config.getDoubleFromExpression("amount", player)

        tracker[player.uniqueId][identifiers.uuid] = AddedPoint(
            point,
            amount
        )

        player.points[point] += amount
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val player = dispatcher.get<Player>() ?: return

        val addedPoint = tracker[player.uniqueId][identifiers.uuid] ?: return
        tracker[player.uniqueId].remove(identifiers.uuid)

        player.points[addedPoint.point] -= addedPoint.amount
    }

    private data class AddedPoint(
        val point: String,
        val amount: Double
    )
}
