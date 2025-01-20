package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.map.listMap
import com.willfp.eco.util.TelekinesisUtils
import com.willfp.eco.util.tryAsPlayer
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import java.util.UUID

object EffectTelekinesis : Effect<NoCompileData>("telekinesis") {
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

    @EventHandler(
        priority = EventPriority.HIGH,
        ignoreCancelled = true
    )
    fun handle(event: BlockDropItemEvent) {
        val player = event.player
        val block = event.block

        if (!plugin.configYml.getBool("effects.telekinesis.always-process-blocks")
            && !TelekinesisUtils.testPlayer(player)) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        val drops = event.items.map { it.itemStack }
        event.items.clear()

        DropQueue(player)
            .setLocation(block.location)
            .addItems(drops)
            .push()
    }

    @EventHandler(
        priority = EventPriority.HIGH,
        ignoreCancelled = true
    )
    fun handle(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (!TelekinesisUtils.testPlayer(player)) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        // Filter out telekinesis spawner xp to prevent dupe
        if (block.type == Material.SPAWNER) {
            event.expToDrop = 0
        }

        DropQueue(player)
            .setLocation(block.location)
            .addXP(event.expToDrop)
            .push()

        event.expToDrop = 0
    }


    @EventHandler(
        priority = EventPriority.HIGH,
        ignoreCancelled = true
    )
    fun handle(event: EntityDeathByEntityEvent) {
        val victim = event.victim

        if (victim is Player && plugin.configYml.getBool("effects.telekinesis.on-players")) {
            return
        }

        val player = event.killer.tryAsPlayer() ?: return

        if (!TelekinesisUtils.testPlayer(player)) {
            return
        }

        val xp = event.xp
        val drops = event.drops

        drops.removeAll { it == null }

        DropQueue(player)
            .addItems(drops)
            .setLocation(victim.location)
            .addXP(xp)
            .forceTelekinesis()
            .push()

        event.deathEvent.droppedExp = 0
        event.deathEvent.drops.clear()
    }
}
