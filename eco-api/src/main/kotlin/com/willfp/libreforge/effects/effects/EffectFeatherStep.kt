package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.getEffectAmount
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.UUID

class EffectFeatherStep : Effect("feather_step") {
    private val players = mutableMapOf<UUID, MutableList<UUID>>()

    override fun handleEnable(player: Player, config: Config) {
        val existing = players[player.uniqueId] ?: mutableListOf()
        existing.add(this.getUUID(player.getEffectAmount(this)))
        players[player.uniqueId] = existing
    }

    override fun handleDisable(player: Player) {
        val existing = players[player.uniqueId] ?: mutableListOf()
        existing.remove(this.getUUID(player.getEffectAmount(this)))
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

    override fun validateConfig(config: Config): List<ConfigViolation> {
        return mutableListOf()
    }
}
