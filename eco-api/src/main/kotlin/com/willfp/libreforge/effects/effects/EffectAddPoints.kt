package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.getEffectAmount
import com.willfp.libreforge.givePoints
import com.willfp.libreforge.takePoints
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import java.util.UUID

class EffectAddPoints : Effect("add_points") {
    private val tracker = mutableMapOf<UUID, MutableMap<UUID, AddedPoint>>()

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        player.givePoints(config.getString("type"), config.getDoubleFromExpression("amount", player))
    }

    override fun handleEnable(
        player: Player,
        config: Config
    ) {
        val added = tracker[player.uniqueId] ?: mutableMapOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        val point = config.getString("type")
        val amount = config.getDoubleFromExpression("amount", player)

        added[uuid] = AddedPoint(
            point,
            amount
        )

        player.givePoints(point, amount)

        tracker[player.uniqueId] = added
    }

    override fun handleDisable(player: Player) {
        val added = tracker[player.uniqueId] ?: mutableMapOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        val addedPoint = added[uuid] ?: return
        added.remove(uuid)

        player.takePoints(addedPoint.point, addedPoint.amount)

        tracker[player.uniqueId] = added
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("type")) violations.add(
            ConfigViolation(
                "type",
                "You must specify the points type!"
            )
        )

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of points!"
            )
        )

        return violations
    }

    private data class AddedPoint(
        val point: String,
        val amount: Double
    )
}
