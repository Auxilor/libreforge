package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import com.willfp.libreforge.triggers.impl.TriggerBowAttack
import com.willfp.libreforge.triggers.impl.TriggerMeleeAttack
import com.willfp.libreforge.integrations.paper.impl.TriggerTridentAttack
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.UUID

object TriggerPlaceholderHits : TriggerPlaceholder("hits") {
    private const val HITS_META_KEY = "libreforge_tracked_hits"

    private val placeholdersHits = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.hits") ?: emptyList()

    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val victim = data.victim ?: return emptyList()
        val player = data.player ?: return emptyList()

        return listOfNotNull(if (placeholdersHits.isNotEmpty()) NamedValue(placeholdersHits, victim.getHits(player)) else null)
    }

    @EventHandler
    fun trackHits(event: TriggerDispatchEvent) {
        if (event.trigger.trigger !in listOf(
                TriggerMeleeAttack,
                TriggerBowAttack,
                TriggerTridentAttack
            )
        ) {
            return
        }

        val player = event.trigger.data.player ?: return
        val entity = event.trigger.data.victim ?: return

        @Suppress("UNCHECKED_CAST")
        val map = entity.getMetadata(HITS_META_KEY).firstOrNull()?.value() as? MutableMap<UUID, Int> ?: mutableMapOf()
        val hits = entity.getHits(player)
        if (entity.health >= entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value) {
            map[player.uniqueId] = 1
        } else {
            map[player.uniqueId] = hits + 1
        }

        entity.removeMetadata(HITS_META_KEY, plugin)
        entity.setMetadata(HITS_META_KEY, plugin.createMetadataValue(map))
    }

    private fun LivingEntity.getHits(player: Player): Int {
        @Suppress("UNCHECKED_CAST")
        val map = this.getMetadata(HITS_META_KEY).firstOrNull()?.value() as? MutableMap<UUID, Int> ?: mutableMapOf()
        return map[player.uniqueId] ?: 0
    }
}
