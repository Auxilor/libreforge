package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.givePoints
import com.willfp.libreforge.takePoints
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import java.util.UUID

class EffectAddPoints : Effect("add_points") {
    override val arguments = arguments {
        require("type", "You must specify the type of points!")
        require("amount", "You must specify the amount of points!")
    }

    private val tracker = mutableMapOf<UUID, MutableMap<UUID, AddedPoint>>()

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        player.givePoints(config.getString("type"), config.getDoubleFromExpression("amount", data))
    }

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val added = tracker[player.uniqueId] ?: mutableMapOf()
        val uuid = identifiers.uuid
        val point = config.getString("type")
        val amount = config.getDoubleFromExpression("amount", player)

        added[uuid] = AddedPoint(
            point,
            amount
        )

        player.givePoints(point, amount)

        tracker[player.uniqueId] = added
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val added = tracker[player.uniqueId] ?: mutableMapOf()
        val uuid = identifiers.uuid
        val addedPoint = added[uuid] ?: return
        added.remove(uuid)

        player.takePoints(addedPoint.point, addedPoint.amount)

        tracker[player.uniqueId] = added
    }

    private data class AddedPoint(
        val point: String,
        val amount: Double
    )
}
