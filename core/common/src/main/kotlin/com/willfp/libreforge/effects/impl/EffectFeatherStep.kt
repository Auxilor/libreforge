package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.UUID

object EffectFeatherStep : Effect<NoCompileData>("feather_step") {
    private val players = listMap<UUID, UUID>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        players[dispatcher.uuid].add(identifiers.uuid)
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        players[dispatcher.uuid].remove(identifiers.uuid)
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEvent) {
        if (event.action != Action.PHYSICAL) {
            return
        }

        val player = event.player

        // Extra check for pressure plates
        if (player.location.block.type in Tag.PRESSURE_PLATES.values
            || player.location.subtract(0.0, 1.0, 0.0).block.type in Tag.PRESSURE_PLATES.values
        ) {
            return
        }

        if (players[player.uniqueId].isNotEmpty()) {
            event.isCancelled = true
        }
    }
}
