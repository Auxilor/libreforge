package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.*

class EffectBlockCommands : Effect(
    "block_commands"
) {
    private val players = mutableMapOf<UUID, MutableMap<UUID, List<String>>>()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val commands = players[player.uniqueId] ?: mutableMapOf()
        commands[identifiers.uuid] = config.getStrings("commands")
        players[player.uniqueId] = commands
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val existing = players[player.uniqueId] ?: mutableMapOf()
        existing.remove(identifiers.uuid)
        players[player.uniqueId] = existing
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        val effects = players[player.uniqueId] ?: emptyMap()

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

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("commands")) violations.add(
            ConfigViolation(
                "commands",
                "You must specify the commands to block!"
            )
        )

        return violations
    }
}