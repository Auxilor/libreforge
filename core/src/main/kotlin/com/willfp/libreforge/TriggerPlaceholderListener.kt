package com.willfp.libreforge

import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import com.willfp.libreforge.triggers.impl.TriggerBowAttack
import com.willfp.libreforge.triggers.impl.TriggerMeleeAttack
import com.willfp.libreforge.triggers.impl.TriggerTridentAttack
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object TriggerPlaceholderListener : Listener {

    @EventHandler
    fun handle(event: TriggerDispatchEvent) {
        val data = event.trigger.data

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

        val hits =
            if (entity.health >= entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value) {
                1
            } else {
                entity.getHits(player)
            }

        entity.pdc.setInt(NamespacedKey(plugin, "HITS:${player.uniqueId}"), hits)
    }

    private fun LivingEntity.getHits(player: Player): Int {
        return this.pdc.getInt(NamespacedKey(plugin, "HITS:${player.uniqueId}")) ?: 0
    }
}
