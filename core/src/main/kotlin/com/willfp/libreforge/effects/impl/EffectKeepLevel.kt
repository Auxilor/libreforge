package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.UUID

object EffectKeepLevel : Effect<NoCompileData>("keep_level") {
    private val players = listMap<UUID, UUID>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        players[dispatcher.uuid] += identifiers.uuid
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        players[dispatcher.uuid] -= identifiers.uuid
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerDeathEvent) {
        val player = event.player

        if (players[player.uniqueId].isNotEmpty()) {
            event.keepLevel = true
            event.droppedExp = 0
        }
    }
}
