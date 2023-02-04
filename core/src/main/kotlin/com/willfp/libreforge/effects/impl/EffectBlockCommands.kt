package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.DefaultHashMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.UUID

object EffectBlockCommands : Effect<NoCompileData>("block_commands") {
    override val arguments = arguments {
        require("commands", "You must specify the commands to block!")
    }

    private val players = DefaultHashMap<UUID, MutableMap<UUID, List<String>>>(mutableMapOf())

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, compileData: NoCompileData) {
        val commands = players[player.uniqueId]
        commands[identifiers.uuid] = config.getStrings("commands")
        players[player.uniqueId] = commands
    }

    override fun onDisable(player: Player, identifiers: Identifiers) {
        val existing = players[player.uniqueId]
        existing.remove(identifiers.uuid)
        players[player.uniqueId] = existing
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        val effects = players[player.uniqueId]

        var command = event.message.split(" ").getOrNull(0) ?: return
        if (command.startsWith("/")) {
            command = command.substring(1)
        }

        for (list in effects.values) {
            for (s in list) {
                if (s.equals(command, ignoreCase = true)) {
                    event.isCancelled = true
                }
            }
        }
    }
}
