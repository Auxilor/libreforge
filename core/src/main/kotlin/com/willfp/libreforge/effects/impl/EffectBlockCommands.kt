package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.nestedListMap
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
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

    private val players = nestedListMap<UUID, UUID, String>()
    private var message: String = ""

    override fun onEnable(player: Player, config: Config, identifiers: Identifiers, holder: ProvidedHolder, compileData: NoCompileData) {
        val commands = players[player.uniqueId]
        commands[identifiers.uuid] = config.getStrings("commands")
        players[player.uniqueId] = commands
        message = config.getString("message")
            .replace("%player%", player.name)
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
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
                    if (message.isNotBlank()) {
                        PlayerUtils.getAudience(player)
                            .sendMessage(StringUtils.toComponent(message.replace("%command%", command)
                                .formatEco(player, true)))
                    }
                }
            }
        }
    }
}
