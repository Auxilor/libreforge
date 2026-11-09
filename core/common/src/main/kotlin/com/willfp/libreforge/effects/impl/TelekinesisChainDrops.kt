package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.cache.EcoCache
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.TelekinesisUtils
import com.willfp.libreforge.BlockCascades
import com.willfp.libreforge.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.player.PlayerHarvestBlockEvent
import java.time.Duration
import java.util.UUID

/**
 * Telekinesis for drops that no player break event covers: blocks that break
 * in a cascade after the broken block, and right click harvests.
 */
internal object TelekinesisChainDrops : Listener {
    private val chainBrokenBlocks = EcoCache.builder<Location, UUID>()
        .expireAfterWrite(Duration.ofSeconds(5))
        .maxSize(10_000)
        .build()

    fun track(player: Player, broken: Block) {
        for (block in BlockCascades.dependents(broken)) {
            chainBrokenBlocks.put(block.location, player.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onItemSpawn(event: ItemSpawnEvent) {
        val location = event.location.block.location
        val player = chainBrokenBlocks.get(location)?.let { Bukkit.getPlayer(it) }

        if (player == null || !TelekinesisUtils.testPlayer(player)) {
            return
        }

        val drop = event.entity.itemStack
        event.isCancelled = true

        plugin.scheduler.on(player).run {
            DropQueue(player)
                .setLocation(location)
                .addItems(listOf(drop))
                .forceTelekinesis()
                .push()
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHarvest(event: PlayerHarvestBlockEvent) {
        val player = event.player
        val drops = event.itemsHarvested.toList()

        if (drops.isEmpty()
            || !TelekinesisUtils.testPlayer(player)
            || !AntigriefManager.canBreakBlock(player, event.harvestedBlock)
        ) {
            return
        }

        DropQueue(player)
            .setLocation(event.harvestedBlock.location)
            .addItems(drops)
            .forceTelekinesis()
            .push()

        event.itemsHarvested.clear()
    }
}
