package com.willfp.libreforge

import com.willfp.libreforge.events.TriggerPreProcessEvent
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID

private lateinit var key: String

class HitsTracker(
    private val plugin: LibReforgePlugin
) : Listener {
    init {
        key = "${plugin.name.lowercase()}_tracked_hits"
    }

    @EventHandler
    fun handle(event: TriggerPreProcessEvent) {
        if (event.trigger !in listOf(
                Triggers.MELEE_ATTACK,
                Triggers.BOW_ATTACK,
                Triggers.TRIDENT_ATTACK
            )
        ) {
            return
        }

        val player = event.player
        val entity = event.data.victim ?: return

        @Suppress("UNCHECKED_CAST")
        val map = entity.getMetadata(key).firstOrNull() as? MutableMap<UUID, Int> ?: mutableMapOf()
        val hits = entity.getHits(player)
        if (entity.health >= entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value) {
            map[player.uniqueId] = 1
        } else {
            map[player.uniqueId] = hits + 1
        }

        entity.removeMetadata(key, plugin)
        entity.setMetadata(key, plugin.createMetadataValue(map))
    }
}

fun LivingEntity.getHits(player: Player): Int {
    @Suppress("UNCHECKED_CAST")
    val map = this.getMetadata(key).firstOrNull() as? MutableMap<UUID, Int> ?: mutableMapOf()
    return map[player.uniqueId] ?: 0
}
