package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player
import java.util.UUID

class EffectFlight : Effect("flight") {
    private val players = mutableMapOf<UUID, List<UUID>>()

    override fun handleEnable(player: Player, config: Config, identifiers: Identifiers) {
        players[player.uniqueId] = players.getOrDefault(player.uniqueId, emptyList()) + identifiers.uuid

        player.isFlying = players[player.uniqueId]?.isNotEmpty() == true
    }

    override fun handleDisable(player: Player, identifiers: Identifiers) {
        players[player.uniqueId] = players.getOrDefault(player.uniqueId, emptyList()) - identifiers.uuid

        player.isFlying = players[player.uniqueId].isNullOrEmpty()
    }
}
