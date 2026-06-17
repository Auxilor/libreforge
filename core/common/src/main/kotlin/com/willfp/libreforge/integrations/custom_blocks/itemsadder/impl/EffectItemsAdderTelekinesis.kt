package com.willfp.libreforge.integrations.custom_blocks.itemsadder.impl

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
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.ItemSpawnEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object EffectItemsAdderTelekinesis : Effect<NoCompileData>("telekinesis") {
    override val description = "Causes ItemsAdder custom block drops to go directly into the player's inventory instead of dropping on the ground."
    override val categories = setOf("inventory")

    private val players = listMap<UUID, UUID>()

    private val pendingLocations = ConcurrentHashMap<Location, UUID>()

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
    fun handle(event: CustomBlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (!TelekinesisUtils.testPlayer(player)) return
        if (!AntigriefManager.canBreakBlock(player, block)) return
        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) return

        val loc = block.location
        pendingLocations[loc] = player.uniqueId
        plugin.scheduler.runLater(2) { pendingLocations.remove(loc) }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: ItemSpawnEvent) {
        val itemLoc = event.entity.location
        val world = itemLoc.world ?: return

        val entry = pendingLocations.entries.firstOrNull { (blockLoc, _) ->
            blockLoc.world == world && itemLoc.distanceSquared(blockLoc.clone().add(0.5, 0.5, 0.5)) <= 4.0
        } ?: return

        val player = Bukkit.getPlayer(entry.value) ?: return

        event.isCancelled = true

        DropQueue(player)
            .setLocation(itemLoc)
            .addItems(listOf(event.entity.itemStack))
            .forceTelekinesis()
            .push()
    }
}
