package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.cache.EcoCache
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.map.concurrentListMap
import com.willfp.eco.util.TelekinesisUtils
import com.willfp.libreforge.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.data.type.Leaves
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.inventory.ItemStack
import java.time.Duration
import java.util.UUID

/**
 * Telekinesis for drops from leaves that decay after a player cuts a tree,
 * through vanilla decay or through datapacks that remove the leaves themselves.
 */
internal object TelekinesisLeafDecay : Listener {
    private const val LEAF_RADIUS = 6

    private val players = concurrentListMap<UUID, UUID>()

    private val playerCutLeaves = EcoCache.builder<Location, UUID>()
        .expireAfterWrite(Duration.ofMinutes(10))
        .maxSize(100_000)
        .build()

    private val pushingOwnDrops = ThreadLocal.withInitial { false }

    fun enable(player: UUID, effect: UUID) {
        players[player].add(effect)
    }

    fun disable(player: UUID, effect: UUID) {
        players[player].remove(effect)
    }

    fun track(player: Player, broken: Block) {
        if (!Tag.LOGS.isTagged(broken.type) || players[player.uniqueId].isEmpty()) {
            return
        }

        for (x in -LEAF_RADIUS..LEAF_RADIUS) {
            for (y in -LEAF_RADIUS..LEAF_RADIUS) {
                for (z in -LEAF_RADIUS..LEAF_RADIUS) {
                    val leaf = broken.getRelative(x, y, z)
                    if ((leaf.blockData as? Leaves)?.isPersistent == false) {
                        playerCutLeaves.put(leaf.location, player.uniqueId)
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onLeafDecay(event: LeavesDecayEvent) {
        val block = event.block
        val player = ownerOf(block)
        playerCutLeaves.invalidate(block.location)

        if (player == null) {
            return
        }

        val drops = block.drops.toList()
        event.isCancelled = true
        block.type = Material.AIR

        send(player, block, drops)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onItemSpawn(event: ItemSpawnEvent) {
        val block = event.location.block

        if (pushingOwnDrops.get() || (block.blockData as? Leaves)?.isPersistent != false) {
            return
        }

        val player = ownerOf(block) ?: return
        event.isCancelled = true

        send(player, block, listOf(event.entity.itemStack))
    }

    private fun ownerOf(block: Block): Player? = playerCutLeaves.get(block.location)
        ?.let { Bukkit.getPlayer(it) }
        ?.takeIf { players[it.uniqueId].isNotEmpty() && TelekinesisUtils.testPlayer(it) }

    private fun send(player: Player, block: Block, drops: List<ItemStack>) {
        plugin.scheduler.on(player).run {
            pushingOwnDrops.set(true)
            try {
                DropQueue(player)
                    .setLocation(block.location)
                    .addItems(drops)
                    .forceTelekinesis()
                    .push()
            } finally {
                pushingOwnDrops.set(false)
            }
        }
    }
}
