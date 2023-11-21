package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.nestedListMap
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.triggers.Dispatcher
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.UUID

object EffectBlockCommands : Effect<NoCompileData>("block_commands") {
    override val arguments = arguments {
        require("commands", "You must specify the commands to block!")
    }

    private val players = nestedListMap<UUID, UUID, String>()
    private val messages = mutableMapOf<UUID, List<String>?>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val commands = players[dispatcher.uuid]
        commands[identifiers.uuid] = config.getStrings("commands")
        messages[identifiers.uuid] = config.getStringsOrNull("messages")

        players[dispatcher.uuid] = commands
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        players[dispatcher.uuid].remove(identifiers.uuid)
        messages.remove(identifiers.uuid)
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        val effects = players[player.uniqueId]

        var command = event.message.split(" ").getOrNull(0) ?: return
        if (command.startsWith("/")) {
            command = command.substring(1)
        }

        for ((uuid, list) in effects) {
            for (s in list) {
                if (s.equals(command, ignoreCase = true)) {
                    event.isCancelled = true
                    val messages = messages[uuid] ?: return

                    if (messages.isNotEmpty()) {
                        for (message in messages) {
                            player.sendMessage(message.formatEco(player))
                        }
                    }

                    return
                }
            }
        }
    }
}
