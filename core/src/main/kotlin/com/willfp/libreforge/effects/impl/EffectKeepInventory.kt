package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.UUID

object EffectKeepInventory : Effect<NoCompileData>("keep_inventory") {
    private val players = listMap<UUID, UUID>()

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, compileData: NoCompileData) {
        players[player.uniqueId] += identifiers.uuid
    }

    override fun onDisable(player: Player, identifiers: Identifiers) {
        players[player.uniqueId] -= identifiers.uuid
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerDeathEvent) {
        val player = event.player

        if (players[player.uniqueId].isNotEmpty()) {
            event.keepInventory = true
            event.drops.clear()
        }
    }
}
