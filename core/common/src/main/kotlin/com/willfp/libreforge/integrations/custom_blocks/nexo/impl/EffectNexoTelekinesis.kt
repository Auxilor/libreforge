package com.willfp.libreforge.integrations.custom_blocks.nexo.impl

import com.nexomc.nexo.api.events.custom_block.NexoBlockBreakEvent
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent
import com.nexomc.nexo.utils.drops.Drop
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.map.listMap
import com.willfp.eco.util.TelekinesisUtils
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import java.util.UUID

object EffectNexoTelekinesis : Effect<NoCompileData>("telekinesis") {
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

    override fun postRegister() {
        TelekinesisUtils.registerTest { players[it.uniqueId].isNotEmpty() }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun NexoFurnitureBreakEvent.handle() {
        if (!TelekinesisUtils.testPlayer(player)) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, baseEntity.location.block)) {
            return
        }

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        val drops = drop.loots.map { it.itemStack() }
        drop = Drop.emptyDrop()

        DropQueue(player)
            .addItems(drops)
            .forceTelekinesis()
            .push()

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun NexoBlockBreakEvent.handle() {
        if (!TelekinesisUtils.testPlayer(player)) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        val drops = drop.loots.map { it.itemStack() }
        drop = Drop.emptyDrop()

        DropQueue(player)
            .setLocation(block.location)
            .addItems(drops)
            .forceTelekinesis()
            .push()
    }
}