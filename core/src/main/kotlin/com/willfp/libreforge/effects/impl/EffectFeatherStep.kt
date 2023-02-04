package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.UUID

class EffectFeatherStep : Effect("feather_step") {
    private val players = mutableMapOf<UUID, MutableList<UUID>>()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val existing = players[player.uniqueId] ?: mutableListOf()
        existing.add(identifiers.uuid)
        players[player.uniqueId] = existing
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val existing = players[player.uniqueId] ?: mutableListOf()
        existing.remove(identifiers.uuid)
        players[player.uniqueId] = existing
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEvent) {
        if (event.action != Action.PHYSICAL) {
            return
        }

        val player = event.player

        if ((players[player.uniqueId] ?: emptyList()).isNotEmpty()) {
            event.isCancelled = true
        }
    }
}
