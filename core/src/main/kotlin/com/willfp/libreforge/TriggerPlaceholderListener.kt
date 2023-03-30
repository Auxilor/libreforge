package com.willfp.libreforge

import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import com.willfp.libreforge.triggers.impl.TriggerBowAttack
import com.willfp.libreforge.triggers.impl.TriggerMeleeAttack
import com.willfp.libreforge.triggers.impl.TriggerTridentAttack
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID

object TriggerPlaceholderListener : Listener {
    private const val HITS_META_KEY = "libreforge_tracked_hits"

    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val data = event.trigger.data

        for (placeholder in event.trigger.data.holder.generatePlaceholders()) {
            event.trigger.addPlaceholder(placeholder)
        }

        if (data.text != null) {
            event.trigger.addPlaceholder(
                NamedValue(
                    listOf("text", "string", "message"),
                    data.text
                )
            )
        }

        event.trigger.addPlaceholder(
            NamedValue(
                listOf("trigger_value", "triggervalue", "trigger", "value", "tv", "v", "t"),
                event.trigger.data.value
            )
        )

        if (data.victim != null) {
            event.trigger.addPlaceholder(
                NamedValue(
                    "victim_health",
                    data.victim.health
                )
            )

            event.trigger.addPlaceholder(
                NamedValue(
                    "victim_max_health",
                    data.victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0
                )
            )

            if (data.player != null) {
                event.trigger.addPlaceholder(
                    NamedValue(
                        "distance",
                        data.victim.location.toVector().distance(data.player.location.toVector())
                    )
                )

                event.trigger.addPlaceholder(
                    NamedValue(
                        "hits",
                        data.victim.getHits(data.player)
                    )
                )
            }
        }
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
