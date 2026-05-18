package com.willfp.libreforge.filters

import com.willfp.libreforge.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.metadata.FixedMetadataValue

object DropProvenanceListener : Listener {
    const val DROPPER_KEY = "libreforge:dropper_uuid"

    @EventHandler(ignoreCancelled = true)
    fun onDrop(event: PlayerDropItemEvent) {
        event.itemDrop.setMetadata(
            DROPPER_KEY,
            FixedMetadataValue(plugin, event.player.uniqueId.toString())
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val world = player.world
        val location = player.location
        val uuid = player.uniqueId.toString()

        val drops = event.drops.toList()
        event.drops.clear()

        for (stack in drops) {
            val item = world.dropItemNaturally(location, stack)
            item.setMetadata(DROPPER_KEY, FixedMetadataValue(plugin, uuid))
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onMerge(event: ItemMergeEvent) {
        val target = event.target
        if (target.hasMetadata(DROPPER_KEY)) return

        val source = event.entity
        val sourceUuid = source.getMetadata(DROPPER_KEY)
            .firstOrNull()?.asString() ?: return

        target.setMetadata(DROPPER_KEY, FixedMetadataValue(plugin, sourceUuid))
    }
}
